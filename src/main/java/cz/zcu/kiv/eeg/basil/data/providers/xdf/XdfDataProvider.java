package cz.zcu.kiv.eeg.basil.data.providers.xdf;

import cz.zcu.kiv.eeg.basil.data.providers.AbstractDataProvider;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * Created by Tomas Prokop on 05.11.2018.
 */
public class XdfDataProvider extends AbstractDataProvider {
    @Override
    public void run() {
/*        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys\nsys.path.append('pathToModules if they are not there by default')\nimport yourModule");
// execute a function that takes a string and returns a string
        PyObject someFunc = interpreter.get("funcName");
        PyObject result = someFunc.__call__(new PyString("Test!"));
        String realResult = (String) result.__tojava__(String.class);*/
    }

    @Override
    public void stop() {

    }
}
