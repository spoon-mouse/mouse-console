package ui.screen;

import com.mouse.backend.Kit;
import de.vandermeer.asciitable.AsciiTable;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.bitcoinj.core.*;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.input.Input;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import static ui.input.Input.*;
import static ui.table.TxnTable.getTable;


public class LaunchScreen {
    private static Logger log = LoggerFactory.getLogger(LaunchScreen.class);
    private static final String APP_TITLE_LINE = "Spoon Mouse BTC";

    private static TextIO textIO = TextIoFactory.getTextIO();
    private static TextTerminal terminal = textIO.getTextTerminal();

    public enum Choice {WALLET, DIGEST, RESTORE, RENAME, RESEED, LISTEN, DELETE, EXIT}

    public LaunchScreen() {
        Context context = Context.getOrCreate();
        Context.propagate(context);

        Kit.start(new File("./wallet"));
        Runtime.getRuntime().addShutdownHook(new Thread(Kit::stop));
        show();
    }


    public static void show() {
        while (true) {
            try {
                Choice choice = textIO.newEnumInputReader(Choice.class).read(APP_TITLE_LINE);
                switch (choice) {
                    case WALLET:
                        load_wallet();
                        break;
                    case DIGEST:
                        digest();
                        break;
                    case RESTORE:
                        restore();
                        break;
                    case RENAME:
                        reName();
                        break;
                    case RESEED:
                        reSeed();
                        break;
                    case LISTEN:
                        listen();
                        break;
                    case DELETE:
                        delete();
                        break;
                    case EXIT:
                        System.exit(0);
                }
            } catch (Exception e) {
                log.error("{} Error ", LaunchScreen.class.getName(), e);
                terminal.println("Error occurred: " + e.getMessage());
            }
        }
    }

    private static void restore() throws UnreadableWalletException, IOException {
        String walletName = getWalletName();
        Kit.restoreWallet(walletName, Input::getPassword, terminal::println);
    }

    private static void reName() throws UnreadableWalletException, IOException {
        String walletName= getWalletName();
        String newName= getWalletName();
        Kit.reName(walletName, newName);
    }

    private static void delete() throws IOException {
        Kit.deleteWallet(getWalletName());
    }

    private static void listen() {
        Kit.addLoggingInfoForWalletBlockEvents();
    }

    private static void reSeed() {
        String seed_txt = getSeed();
        long epochSeconds = getEpochSeconds();
        String walletName= getWalletName();
        terminal.print("restoring...");
        Kit.restore_from_seed(walletName, seed_txt, epochSeconds, terminal::println);
        terminal.print("restored");
    }


    private static void load_wallet() {
        terminal.print( "wallets: "+ Kit.getWalletNames().stream().sorted().collect(Collectors.joining(" ")) );
        terminal.println();
        String walletName = getWalletName();
        Kit.getWallet(walletName);
        try {
            new WalletScreen(walletName).show();
        } catch (Exception e) {
            log.error("Error occurred in WalletScreen: {}", walletName, e);
            terminal.println("Error WalletScreen: " + walletName+" "+e.getMessage());
        }
    }


    private static void digest() {
        AsciiTable table = getTable("name", "encrypted", "balance", "block hight", "id", "receive address");
        Kit.getMetaWallets().forEach( w -> {
            table.addRow(w.name(), w.isEncrypted(), w.balance(), w.blockHeight(), w.id(), w.receiveAddress());
        });
        table.addRule();
        terminal.println(table.render());
    }


}
