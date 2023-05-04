package com.codeforces.iomarkup.test;

import com.codeforces.commons.compress.ZipUtil;
import com.codeforces.commons.io.FileUtil;
import com.codeforces.commons.io.http.HttpUtil;
import com.codeforces.commons.math.RandomUtil;
import com.codeforces.commons.text.UrlUtil;
import com.codeforces.commons.xml.descriptor.problem.ProblemDescriptorUtil;
import com.codeforces.commons.xml.descriptor.problem.model.ProblemDescriptor;
import com.codeforces.commons.xml.descriptor.problem.model.ProblemTest;
import com.codeforces.commons.xml.descriptor.problem.model.ProblemTestset;
import com.codeforces.commons.xml.descriptor.problem.model.validator.ProblemValidatorTest;
import com.codeforces.interop.invoker.Interop;
import com.codeforces.invoker.ioc.InvokerModule;
import com.codeforces.invoker.services.IRunService;
import com.codeforces.iomarkup.IoMarkup;
import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.exec.TargetLanguage;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ComplexTest {
    private static final Logger logger = LogManager.getLogger(ComplexTest.class);

    private final InvokerModule invokerModule = new InvokerModule();
    private final Injector injector = Guice.createInjector(invokerModule);
    private final IRunService runService = injector.getInstance(IRunService.class);

    @Test
    public void test() throws IOException {
        logger.info("Testing validators");

        for (String name : getMarkupsList()) {
            var contestAndProblem = getContestAndProblem(name);
            logger.info("Testing %s: %s".formatted(
                    name,
                    "https://codeforces.com/problemset/problem/%s/%s".formatted(
                            contestAndProblem.contest,
                            contestAndProblem.problem
                    )
            ));

            var lines = getLines(name);
            var firstLine = lines.get(0);
            firstLine = firstLine.substring(firstLine.indexOf("http"));

            testSingle(name, getPackageUrl(firstLine), String.join(System.lineSeparator(), lines));
        }
    }

    private void testSingle(String name, String packageUrl, String ioMarkupString) {
        var ioMarkup = new IoMarkup(ioMarkupString);

        Path packagePath = getPackagePath(name, packageUrl);
        ProblemDescriptor descriptor = getProblemDescriptor(name, packageUrl);

        testValidator(packagePath, descriptor, ioMarkup);

//        var validator = ioMarkup.generate(TargetComponent.VALIDATOR, TargetLanguage.CPP);
//        assertEquals(1, validator.size());
    }

    @SneakyThrows
    private void testValidator(Path packagePath, ProblemDescriptor problemDescriptor, IoMarkup ioMarkup) {
        Assertions.assertTrue(problemDescriptor.isTestsWellformed());

        var validators = problemDescriptor.getValidators();
        Assertions.assertEquals(1, validators.size());

        Collection<TranslatedFile> generatedFiles = ioMarkup.generate(TargetComponent.VALIDATOR, TargetLanguage.CPP);
        Assertions.assertEquals(1, generatedFiles.size());

        var generatedFile = generatedFiles.stream().findAny().get();

        logger.info("Generated validator: " + generatedFile.content());

        var validator = validators.get(0);
        Path validatorPath = packagePath.resolve(validator.getSourceFile().getPath());

        var validatorTestset = validator.getTestset();
        logger.info("Testing validator tests");
        for (int i = 1; validatorTestset != null && i <= validatorTestset.getTestCount(); ++i) {
            logger.info("Testing validator test #" + i);
            var test = validatorTestset.getTests().get(i - 1);
            if (test.getVerdict() != ProblemValidatorTest.Verdict.VALID)
                continue;

            Interop.RunRequest request = Interop.RunRequest.newBuilder()
                    .setRequestId(RandomUtil.getRandomToken())
                    .setFile(
                            Interop.File.newBuilder()
                                    .setName(generatedFile.name())
                                    .setType("cpp.g++17")
                                    .setContent(
                                            Interop.Blob.newBuilder()
                                                    .setData(ByteString.copyFromUtf8(generatedFile.content()))
                                    )
                    )
                    .addResource(getTestlib(packagePath, problemDescriptor))
                    .setRunParams(
                            Interop.RunParams.newBuilder()
                                    .setTimeLimit(60000)
                                    .setMemoryLimit(FileUtil.BYTES_PER_GB)
                    )
                    .setInput(
                            Interop.Input.newBuilder()
                                    .setTextInput(
                                            Interop.Blob.newBuilder()
                                                    .setData(ByteString.copyFrom(Files.readAllBytes(
                                                            packagePath.resolve(validatorTestset.getInputPath(i)))
                                                    ))
                                    )
                    )
                    .build();

            var response = runService.run(request, null);
            var processResult = response.getProcessResult();
            Assertions.assertEquals(Interop.ProcessResult.Verdict.OK, processResult.getVerdict());
            Assertions.assertEquals(0, processResult.getExitCode());
        }

        logger.info("Testing real tests");
        for (ProblemTestset testset : problemDescriptor.getTestsets()) {
            for (int i = 1; i <= testset.getTestCount(); ++i) {
                logger.info("Testset " + testset.getName() + ": test#" + i);

                Interop.RunRequest.Builder request = Interop.RunRequest.newBuilder()
                        .setRequestId(RandomUtil.getRandomToken())
                        .setFile(
                                Interop.File.newBuilder()
                                        .setName(generatedFile.name())
                                        .setType("cpp.g++17")
                                        .setContent(
                                                Interop.Blob.newBuilder()
                                                        .setData(ByteString.copyFromUtf8(generatedFile.content()))
                                        )
                        )
                        .addResource(getTestlib(packagePath, problemDescriptor))
                        .setRunParams(
                                Interop.RunParams.newBuilder()
                                        .setTimeLimit(60000)
                                        .setMemoryLimit(FileUtil.BYTES_PER_GB)
                        );

                var test = testset.getTests().get(i - 1);
                if (test.getMethod() == ProblemTest.Method.MANUAL) {
                    request.setInput(
                            Interop.Input.newBuilder()
                                    .setTextInput(
                                            Interop.Blob.newBuilder()
                                                    .setData(ByteString.copyFrom(Files.readAllBytes(
                                                            packagePath.resolve(testset.getInputPath(i)))
                                                    ))
                                    )
                    );

                    var response = runService.run(request.build(), null);
                    var processResult = response.getProcessResult();
                    Assertions.assertEquals(Interop.ProcessResult.Verdict.OK, processResult.getVerdict());
                    Assertions.assertEquals(0, processResult.getExitCode());
                }
            }
        }
    }

    @SneakyThrows
    private Interop.File getTestlib(Path packagePath, ProblemDescriptor descriptor) {
        var testlib = descriptor.getResources().stream()
                .filter(resource -> "testlib.h".equals(FileUtil.getNameAndExt(Objects.requireNonNull(resource.getPath()))))
                .findAny().get();
        return Interop.File.newBuilder()
                .setName("testlib.h")
                .setType(testlib.getType())
                .setContent(Interop.Blob.newBuilder().setData(
                        ByteString.copyFrom(Files.readAllBytes(packagePath.resolve(testlib.getPath()))))
                ).build();
    }

    @SneakyThrows
    private ProblemDescriptor getProblemDescriptor(String name, String polygonUrl) {
        var packagePath = getPackagePath(name, polygonUrl);
        return ProblemDescriptorUtil.fromXml(Files.readAllBytes(packagePath.resolve("problem.xml")));
    }

    @SneakyThrows
    private Path getPackagePath(String name, String polygonUrl) {
        var path = Profile.getCachePath().resolve(name);
        if (!Files.isDirectory(path))
            Files.deleteIfExists(path);
        if (!Files.exists(path)) {
            Files.createDirectories(path);

            var packageResponse = HttpUtil.executeGetRequestAndReturnResponse(polygonUrl);

            Assertions.assertFalse(packageResponse.hasIoException());
            Assertions.assertEquals(200, packageResponse.getCode());

            ZipUtil.unzip(packageResponse.getBytes(), path.toFile());
        }
        return path;
    }

    private String getPackageUrl(String polygonPackageUrl) {
        return UrlUtil.appendParametersToUrl(polygonPackageUrl,
                "login", "codeforces",
                "password", System.getenv("IO_MARKUP_POLYGON_PASSWORD"));
    }

    private List<String> getMarkupsList() throws IOException {
        return getLines("list.txt");
    }

    private List<String> getLines(String name) throws IOException {
        try (var stream = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(getResourceName(name)))))) {
//        try (var stream = Files.newBufferedReader(
//                Path.of("C:\\Programing\\bachelor-thesis\\code-repo\\code\\src\\test\\resources\\complexTestMarkups").resolve(name),
//                StandardCharsets.UTF_8)) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = stream.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }

    private String getResourceName(String name) {
        return "/complexTestMarkups/" + name;
    }

    private ContestAndProblem getContestAndProblem(String name) {
        StringBuilder contest = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            if (!Character.isDigit(name.charAt(i)))
                break;
            contest.append(name.charAt(i));
        }
        return new ContestAndProblem(contest.toString(), FileUtil.getName(name.substring(contest.length())));
    }

    private record ContestAndProblem(String contest, String problem) {
    }
}
