package com.example.phoneapp.aidl;

interface IDuplicateContactsCallback{
    void onCompleted(int status, int detectedCount, String message);
}