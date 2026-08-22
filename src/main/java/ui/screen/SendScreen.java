package ui.screen;

import com.mouse.backend.Kit;
import com.mouse.backend.csv.CsvTxn;
import com.mouse.backend.txn.*;
import com.mouse.backend.util.CoinSelectOption;
import ui.input.Input;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.bitcoinj.core.*;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ui.table.TxnTable.expanded_transation_table;


public class SendScreen {

    private static Logger log = LoggerFactory.getLogger(SendScreen.class);

    public enum Choice {PENDING, SEND, RBF, CSV,  SWEEP, SELECTOR, VIEW_TXN, BACK, EXIT;}

    private static TextIO textIO = TextIoFactory.getTextIO();
    private static TextTerminal terminal = textIO.getTextTerminal();

    private String walletName;
    private Wallet wallet;
    private PeerGroup pg;

    private Txn txn;

    public SendScreen(String name){
        walletName=name;
        wallet=Kit.wallet(walletName);
        pg=Kit.peerGroup();
    }

    public void show() {
        while(true) {
            Choice choice = textIO.newEnumInputReader(Choice.class).read(walletName+" Transactions");
            try {
                switch (choice) {
                    case PENDING:
                        terminal.println(expanded_transation_table(wallet.getPendingTransactions().stream().toList(), wallet) );
                        break;
                    case SEND:
                        txn = new StdTxn(walletName);
                        txn.setAddress(Input.getAddress()).setAmount(Input.getAmount()).setFee(Input.getFee());
                        TxnInfo sent = txn.send(Input::getPassword, terminal::println);
                        terminal.println(sent.toString());
                        break;
                    case RBF:
                        txn = new RbfTxn(walletName);
                        txn.setTxnId(Input.getTxId()).setFee(Input.getFee());
                        sent = txn.send(Input::getPassword, terminal::println);
                        terminal.println(sent.toString());
                        break;
                    case CSV:
                        txn = new CsvTxn(walletName).setCheckSeqVerDuration(Input.getLockDepth());
                        txn.setAddress(Input.getAddress()).setAmount(Input.getAmount()).setFee(Input.getFee());
                        sent = txn.send(Input::getPassword, terminal::println);
                        terminal.println(sent.toString());
                        break;
                    case SWEEP:
                        break;
                    case SELECTOR:
                        CoinSelectOption option = textIO.newEnumInputReader(CoinSelectOption.class).read("Coin Selector:");
                        txn.setCoinSelector(option);
                        terminal.println("set coin selection by "+option);
                        break;
                    case VIEW_TXN:
                        InfoScreen.view_a_transaction(wallet);
                        break;
                    case BACK:
                        return;
                    case EXIT:
                        System.exit(0);
                }
            } catch (Exception | IllegalAmountException e) {
                terminal.println("ERROR: " + e.getMessage());
                log.error(SendScreen.class.getName()+" Error ", e);
            }
        }
    }

}
