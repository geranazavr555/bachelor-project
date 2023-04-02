#include <bits/stdc++.h>

using namespace std;

int main() {
    cout << "int32 int32" << endl;
    cout << typeid(1 + 1).name() << endl;
    cout << typeid(1 * 1).name() << endl;
    cout << typeid(1 & 1).name() << endl;
    cout << "int32 uint32" << endl;
    cout << typeid(1 + 1u).name() << endl;
    cout << typeid(1 * 1u).name() << endl;
    cout << typeid(1 & 1u).name() << endl;
    cout << "int32 int64" << endl;
    cout << typeid(1 + 1ll).name() << endl;
    cout << typeid(1 * 1ll).name() << endl;
    cout << typeid(1 & 1ll).name() << endl;
    cout << "int32 uint64" << endl;
    cout << typeid(1 + 1ull).name() << endl;
    cout << typeid(1 * 1ull).name() << endl;
    cout << typeid(1 & 1ull).name() << endl;

    cout << "uint32 int32" << endl;
    cout << typeid(1u + 1).name() << endl;
    cout << typeid(1u * 1).name() << endl;
    cout << typeid(1u & 1).name() << endl;
    cout << "uint32 uint32" << endl;
    cout << typeid(1u + 1u).name() << endl;
    cout << typeid(1u * 1u).name() << endl;
    cout << typeid(1u & 1u).name() << endl;
    cout << "uint32 int64" << endl;
    cout << typeid(1u + 1ll).name() << endl;
    cout << typeid(1u * 1ll).name() << endl;
    cout << typeid(1u & 1ll).name() << endl;
    cout << "uint32 uint64" << endl;
    cout << typeid(1u + 1ull).name() << endl;
    cout << typeid(1u * 1ull).name() << endl;
    cout << typeid(1u & 1ull).name() << endl;

    cout << "int64 int32" << endl;
    cout << typeid(1ll + 1).name() << endl;
    cout << typeid(1ll * 1).name() << endl;
    cout << typeid(1ll & 1).name() << endl;
    cout << "int64 uint32" << endl;
    cout << typeid(1ll + 1u).name() << endl;
    cout << typeid(1ll * 1u).name() << endl;
    cout << typeid(1ll & 1u).name() << endl;
    cout << "int64 int64" << endl;
    cout << typeid(1ll + 1ll).name() << endl;
    cout << typeid(1ll * 1ll).name() << endl;
    cout << typeid(1ll & 1ll).name() << endl;
    cout << "int64 uint64" << endl;
    cout << typeid(1ll + 1ull).name() << endl;
    cout << typeid(1ll * 1ull).name() << endl;
    cout << typeid(1ll & 1ull).name() << endl;

    cout << "uint64 int32" << endl;
    cout << typeid(1ull + 1).name() << endl;
    cout << typeid(1ull * 1).name() << endl;
    cout << typeid(1ull & 1).name() << endl;
    cout << "uint64 uint32" << endl;
    cout << typeid(1ull + 1u).name() << endl;
    cout << typeid(1ull * 1u).name() << endl;
    cout << typeid(1ull & 1u).name() << endl;
    cout << "uint64 int64" << endl;
    cout << typeid(1ull + 1ll).name() << endl;
    cout << typeid(1ull * 1ll).name() << endl;
    cout << typeid(1ull & 1ll).name() << endl;
    cout << "uint64 uint64" << endl;
    cout << typeid(1ull + 1ull).name() << endl;
    cout << typeid(1ull * 1ull).name() << endl;
    cout << typeid(1ull & 1ull).name() << endl;

    cout << "float32 float32" << endl;
    cout << typeid(1.0f + 1.0f).name() << endl;
    cout << typeid(1.0f / 1.0f).name() << endl;
    cout << "float32 float64" << endl;
    cout << typeid(1.0f + 1.0).name() << endl;
    cout << typeid(1.0f / 1.0).name() << endl;
    cout << "float64 float32" << endl;
    cout << typeid(1.0 + 1.0f).name() << endl;
    cout << typeid(1.0 / 1.0f).name() << endl;
    cout << "float64 float64" << endl;
    cout << typeid(1.0 + 1.0).name() << endl;
    cout << typeid(1.0 / 1.0).name() << endl;

    cout << "float32 int32" << endl;
    cout << typeid(1.0f + 1).name() << endl;
    cout << typeid(1.0f * 1).name() << endl;
    cout << "float32 uint32" << endl;
    cout << typeid(1.0f + 1u).name() << endl;
    cout << typeid(1.0f * 1u).name() << endl;
    cout << "float32 int64" << endl;
    cout << typeid(1.0f + 1ll).name() << endl;
    cout << typeid(1.0f * 1ll).name() << endl;
    cout << "float32 uint64" << endl;
    cout << typeid(1.0f + 1ull).name() << endl;
    cout << typeid(1.0f * 1ull).name() << endl;
}