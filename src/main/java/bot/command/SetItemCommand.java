package bot.command;

import database.DataBase;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.util.Scanner;

public class SetItemCommand {

    private static boolean active = false;

    public static SendMessage processMessage(Update update, File file){
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUserName(msg);

        SendMessage answer = new SendMessage();
        answer.setChatId(chatId.toString());

        boolean pass = false;
        String itemName = null;
        String sizes = null;
        int price = 0;

        if(update.getMessage().hasPhoto()){
            if(update.getMessage().getCaption() != null){
                String text = update.getMessage().getCaption();
                Scanner scanner = new Scanner(text);
                itemName = scanner.next();
                sizes = scanner.next();
                price = Integer.parseInt(scanner.next());
                System.out.println(itemName + " " + sizes + " " + price);
                if(itemName != null && sizes != null && price != 0){
                    pass = true;
                }
            }
        }

        if(pass){
            String SQL_command = "INSERT INTO Products (" +
                    "image,name,sizes,price" +
                    ") VALUES (" +
                    "FILE_READ(\'" + file.getPath() + "\')" + ",\'" + itemName + "\',\'" + sizes + "\',\'" + price + "\'" +
                    ");";
            answer.setText(DataBase.getInstance().sqlCommandLine(SQL_command) + " Команда выполнена");
            setActive(false);
        }else{
            String standardAnswer = "Не верная команда или не правельный формат сообщения, попробуйте снова";
            answer.setText(standardAnswer);
            setActive(false);
        }

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
        SetItemCommand.active = active;
    }
}
