package ui;

import com.mouse.backend.Kit;
import com.mouse.backend.util.Config;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.screen.LaunchScreen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.mouse.backend.util.Config.DEFAULT_WALLET_NAME;
import static ui.input.Input.getWalletName;

public class ConsoleApp {

    private static Logger log = LoggerFactory.getLogger(LaunchScreen.class);
    private static TextIO textIO = TextIoFactory.getTextIO();
    private static TextTerminal terminal = textIO.getTextTerminal();


    //https://coinfaucet.eu/en/btc-testnet/
    public static final String coin_faucet_return_Address = "tb1qerzrlxcfu24davlur5sqmgzzgsal6wusda40er";


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException{

        Runtime.getRuntime().addShutdownHook(new Thread(Kit::stop));

        String dir="./wallet-"+ Config.NETWORK.name().toLowerCase();

        while (true) {
            try {

                File f = new File(dir);
                Kit.start(f);
                new LaunchScreen();
                Kit.stop();

                File[] directories = new File(".").listFiles(File::isDirectory);

                dir = textIO.newStringInputReader().withDefaultValue(Arrays.toString(directories)).withInputTrimming(true).read("portfolio ");

            } catch (Exception e) {
                log.error("{} Error ", LaunchScreen.class.getName(), e);
                terminal.println("Error occurred: " + e.getMessage());
            }
        }
    }

}
