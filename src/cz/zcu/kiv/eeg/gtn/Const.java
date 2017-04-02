package cz.zcu.kiv.eeg.gtn;

import java.awt.Font;

/**
 * Obsahuje konstanty pou��van� v aplikaci.
 */
public class Const {

    /**
     * Koncovka souboru ve form�tu VDEF.
     */
    public static final String VHDR_EXTENSION = ".vhdr";
    public static final String VMRK_EXTENSION = ".vmrk";
    public static final String EEG_EXTENSION = ".eeg";

    //----------------------Settings constants---------------------------
    public static final String DEF_IP_ADDRESS = "127.0.0.1";
    public static final String[] DEF_PORTS = {"51244"};
    public static final int DEF_PORT = 51244;
    public static final String IPADDRESS_PATTERN
            = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    
   

    //-------------------------MLP classifier------------------------
    public static final int DEFAULT_OUTPUT_NEURONS = 1; /* number of output neurons */

    public static final double LEARNING_RATE = 0.1;     /* learning step */

    public static final int NUMBER_OF_ITERATIONS = 2000;

    //------------------------Classifier training-----------------------
    //public static final String TRAINING_RAW_DATA_FILE_NAME = "data/train/no_artifacts2.dat";
    public static final String TRAINING_RAW_DATA_FILE_NAME = "data/train/no_artifacts2.dat";
    public static final String TRAINING_FILE_NAME = "data/new_models/winnermlpdwt.classifier";
    public static final String INFO_DIR = "data/numbers";

    //----------------------Epoch------------------------
    public static final int PREESTIMULUS_VALUES = 100;
    public static final int POSTSTIMULUS_VALUES = 750;
    public static final int SAMPLING_FQ = 1000;

    //----------------------Buffer-----------------------
    public static final int BUFFER_SIZE = 10000;
    public static final int NUMBER_OF_STIMULUS = 400;

    //----------------------Main window------------------
    public static final String APP_NAME = "Guess the number";

    public static final int MAIN_WINDOW_WIDTH = 960;

    public static final int MAIN_WINDOW_HEIGHT = 380;

    public static final String UNKNOWN_RESULT = "?";

    public static final String RESULT_FONT_NAME = "Arial";

    public static final int RESULT_FONT_SIZE = 250;

    public static final int RESULT_FONT_STYLE = Font.BOLD;

    public static final String[] TABLE_COLUMN_NAMES = {"ID", "Name", "Score", "Trials"};
    
    
    //----------------------Experiment---------------------
    public static final int USED_CHANNELS = 3;
    public static final int GUESSED_NUMBERS = 9;
    
    //---------------------Buffer-------------------
    public static final int RESERVE = 20;

    public static final int ELECTROD_VALS = 20;
    
    //---------------------TEST---------------------
   /* public static final String[] DIRECTORIES = {"data/numbers/Horazdovice", 
        "data/numbers/Blatnice","data/numbers/Strasice","data/numbers/Masarykovo", "data/numbers/Stankov", 
        "data/numbers/17ZS", "data/numbers/DolniBela", "data/numbers/KVary", "data/numbers/SPSD", "data/numbers/Strasice2",
        "data/numbers/Tachov", "data/numbers/Tachov2", "data/numbers/ZSBolevecka"};*/
//    public static final String[] DIRECTORIES = {"data/numbers/Horazdovice",
//        "data/numbers/Blatnice","data/numbers/Strasice","data/numbers/Masarykovo", "data/numbers/Stankov",
//         "data/numbers/DolniBela", "data/numbers/KVary", "data/numbers/SPSD", "data/numbers/Strasice2",
//        "data/numbers/Tachov", "data/numbers/Tachov2", "data/numbers/ZSBolevecka"};
    public static final String[] DIRECTORIES = {"data/numbers"};
}
