package bot.command;

import database.DataBase;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.util.Scanner;

public class GetItemCommand {
    private static boolean active = false;

    public static SendPhoto processMessage(Update update){
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUserName(msg);

        SendPhoto answer = new SendPhoto();
        answer.setChatId(chatId.toString());
        String SQL_get_text = "SELECT name,sizes,price FROM Products WHERE name = \'" + msg.getText() + "\';";
        String SQL_get_file = "SELECT image FROM Products WHERE name = \'" + msg.getText() + "\';";

        Scanner scanner = new Scanner(DataBase.getInstance().sqlCommandLine(SQL_get_text));
        File file = DataBase.getInstance().sqlCommandLineF(SQL_get_file);
        String name = scanner.next();
        String sizes = scanner.next();
        int price = Integer.parseInt(scanner.next());

        answer.setPhoto(new InputFile(file));
        answer.setCaption(name + " " + sizes + " " + price);

        return answer;
    }

    private static String getUserName(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean active) {
        GetItemCommand.active = active;
    }
}
