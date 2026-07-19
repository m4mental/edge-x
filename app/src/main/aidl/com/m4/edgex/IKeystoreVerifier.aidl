package com.m4.edgex;

interface IKeystoreVerifier {
    byte[] sign(in byte[] challenge);
}
