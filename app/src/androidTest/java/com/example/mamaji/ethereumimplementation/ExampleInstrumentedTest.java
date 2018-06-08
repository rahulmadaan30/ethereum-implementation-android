package com.example.mamaji.ethereumimplementation;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.apache.commons.io.IOUtils;
import org.ethereum.jsontestsuite.TestCase;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest extends TestCase{


    public ExampleInstrumentedTest(JSONObject testCaseJSONObj) throws ParseException {
        super(testCaseJSONObj);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.mamaji.ethereumimplementation", appContext.getPackageName());
    } @Test
    public void testCompile() throws IOException {
        compile();
    }


    private void compile() throws IOException {
        InputStream solidityStream =  this.getClass().getResourceAsStream("/MyContract.sol");
        String solidityString = IOUtils.toString(solidityStream);
        SolidityCompiler.Result result = SolidityCompiler.compile(solidityString.getBytes(), true,
                SolidityCompiler.Options.ABI, SolidityCompiler.Options.BIN);
        CompilationResult res = CompilationResult.parse(result.output);
        CompilationResult.ContractMetadata metadata = res.contracts.get("SimpleOwnedStorage");
        System.out.println(metadata.bin);
        System.out.println(metadata.abi);

    }
}
