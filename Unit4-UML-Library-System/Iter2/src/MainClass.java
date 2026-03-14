import com.oocourse.library2.LibraryBookIsbn;
import com.oocourse.library2.LibraryCloseCmd;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryOpenCmd;
import com.oocourse.library2.LibraryReqCmd;

import java.time.LocalDate;
import java.util.Map;

import static com.oocourse.library2.LibraryIO.SCANNER;

public class MainClass {
    public static void main(String[] args) {
        Map<LibraryBookIsbn, Integer> bookList = SCANNER.getInventory();
        Library lib = new Library(bookList);
        while (true) {
            LibraryCommand command = SCANNER.nextCommand();
            if (command == null) { break; }
            LocalDate today = command.getDate(); // 今天的日期
            lib.setDate(today);
            if (command instanceof LibraryOpenCmd) {
                // 在开馆时做点什么
                lib.tidy("OPEN");
            } else if (command instanceof LibraryCloseCmd) {
                // 在闭馆时做点什么
                lib.tidy("CLOSE");
            } else {
                LibraryReqCmd req = (LibraryReqCmd) command;
                LibraryReqCmd.Type type = req.getType(); // 指令对应的类型（查询/阅读/借阅/预约/还书/取书/归还）
                // 对指令进行处理
                switch (type) {
                    case QUERIED:
                        lib.queried(req);
                        break;
                    case BORROWED:
                        lib.borrowed(req);
                        break;
                    case ORDERED:
                        lib.ordered(req);
                        break;
                    case RETURNED:
                        lib.returned(req);
                        break;
                    case PICKED:
                        lib.picked(req);
                        break;
                    case READ:
                        lib.read(req);
                        break;
                    case RESTORED:
                        lib.restored(req);
                        break;
                    default:
                }
            }
        }
    }
}
