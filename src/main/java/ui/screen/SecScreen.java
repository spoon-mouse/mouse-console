package ui.screen;

import com.mouse.backend.Kit;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.bitcoinj.wallet.Wallet;
import ui.input.Input;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class SecScreen {
    public static final String BAD_WALLET_DECRYPTION = "ERROR INVALID PASSWORD: bad wallet decryption";
    public enum Choice { PASSWORD, SEED, BACK, EXIT; }
    private static TextIO textIO = TextIoFactory.getTextIO();
    private static TextTerminal terminal = textIO.getTextTerminal();

    private String walletName;
    private Wallet wallet;

    public SecScreen(String name){
        walletName=name;
        wallet = Kit.getWallet(walletName);
    }

    public void show() throws IOException {
        while(true) {
            Choice choice = textIO.newEnumInputReader(Choice.class).read(walletName+ " Security");
            switch (choice) {
                case PASSWORD:
                    PasswordScreen.show(walletName, wallet);
                    break;
                case SEED:
                    show_wallet_seed();
                    break;
                case BACK:
                    return;
                case EXIT:
                    System.exit(0);
            }
        }
    }


    private void show_wallet_seed() throws IOException {
        terminal.println("WARN showing SEED in plain text for wallet "+walletName);
        terminal.println("Creation Time: " + Kit.getWalletCreationTime(walletName));
        try {
            final List<char[]> walletSeedWords = Kit.getWalletSeed(walletName, Input::getPassword);

            terminal.print("Seed Phrases: ");
            for (char[] seedWord : walletSeedWords) {
                terminal.print(String.valueOf(seedWord)+" ");
            }
            terminal.println();

            for (char[] seedWord : walletSeedWords) {
                Arrays.fill(seedWord, '\0');
            }

        } catch (NoSuchAlgorithmException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
