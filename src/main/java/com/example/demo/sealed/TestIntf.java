package com.example.demo.sealed;

@SuppressWarnings("preview")
public sealed interface TestIntf permits TestImplClass {
	String getValue();
}

