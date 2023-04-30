package com.codeforces.iomarkup.daemon;

import com.codeforces.interop.iomarkup.Interop;
import com.codeforces.interop.iomarkup.InteropRpc;
import com.codeforces.iomarkup.IoMarkup;
import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.exec.TargetLanguage;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class InteropRpcImpl implements InteropRpc {
    private static final Logger logger = LogManager.getLogger(InteropRpcImpl.class);

    @Override
    public byte[] generate(byte[] requestBytes) {
        Interop.GenerateRequest request;
        var response = Interop.GenerateResponse.newBuilder();
        try {
            request = Interop.GenerateRequest.parseFrom(requestBytes);
        } catch (InvalidProtocolBufferException e) {
            logger.error("InvalidProtocolBufferException", e);
            response.setErrorMessage(nullToEmpty(e.getMessage()));
            return response.build().toByteArray();
        }

        String ioMarkupString = request.getIoMarkup();
        IoMarkup ioMarkup;
        try {
            ioMarkup = new IoMarkup(ioMarkupString);
        } catch (RuntimeException e) {
            logger.error("RuntimeException", e);
            response.setErrorMessage(nullToEmpty(e.getMessage()));
            return response.build().toByteArray();
        }

        for (var targetFileDescription : request.getTargetFileDescriptionList()) {
            var component = convert(TargetComponent.class, targetFileDescription.getTargetComponent());
            var language = convert(TargetLanguage.class, targetFileDescription.getTargetLanguage());

            Collection<TranslatedFile> result;
            try {
                result = ioMarkup.generate(component, language);
            } catch (RuntimeException e) {
                logger.error("RuntimeException", e);
                response.setErrorMessage(nullToEmpty(e.getMessage()));
                return response.build().toByteArray();
            }

            for (TranslatedFile translatedFile : result) {
                response.addResultFile(
                        Interop.ResultFile.newBuilder()
                                .setComponent(targetFileDescription.getTargetComponent())
                                .setLanguage(targetFileDescription.getTargetLanguage())
                                .setName(translatedFile.name())
                                .setContent(translatedFile.content())
                                .build()
                );
            }
        }

        return response.build().toByteArray();
    }

    private <A extends Enum<A>, B extends Enum<B>> B convert(Class<B> targetClass, A value) {
        return Enum.valueOf(targetClass, value.name());
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
