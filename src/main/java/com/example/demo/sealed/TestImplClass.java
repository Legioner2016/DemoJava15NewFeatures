package com.example.demo.sealed;

@SuppressWarnings("preview")
public non-sealed class TestImplClass implements TestIntf, AnotherIntf {

	@Override
	public String getValue() {
		return "some value";
	}

}
