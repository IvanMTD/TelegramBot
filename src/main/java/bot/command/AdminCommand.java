package bot.command;

import database.DataBase;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AdminCommand extends BotCommand implements IBotCommand {

    private final String IDENTIFIER;
    private static final String PASSWORD = "mor911";

    private static boolean active;
    private static boolean pass;

    public AdminCommand(String identifier, String description){
        super(identifier,description);
        IDENTIFIER = identifier;
        active = false;
        pass = false;
    }

    @Override
    public String getCommandIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        SendMessage msg = new SendMessage();
        msg.setChatId(message.getChatId().toString());
        if(active){
            msg.setText("Вы вышли из режима \"прямых комманд\" в базу данных SQL");
            active = false;
            pass = false;
        }else {
            msg.setText("Введите пароль администратора: ");
            active = true;
        }
        try {
            absSender.execute(msg);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    public static SendMessage processMessage(Update update){
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUserName(msg);
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId.toString());

        if(pass) {
            answer.setText(DataBase.getInstance().sqlCommandLine(msg.getText()));
        }

        if(msg.getText().equals(PASSWORD)){
            answer.setText("Вы вошли в режим \"прямых комманд\" в базу данных SQL");
            pass = true;
        }else{
            if(!pass) {
                answer.setText("Вы ввели не верный пароль, попробуйте еще раз");
            }
        }

        return answer;
    }

    private static String getUserName(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    public static boolean isActive(){
        return active;
    }
}
