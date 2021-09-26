package bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class NonCommand {
    public static SendMessage getNonCommandAnswer(Update update){

        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUserName(msg);

        SendMessage answer = new SendMessage();
        answer.setChatId(chatId.toString());

        if(update.hasMessage() && update.getMessage().getText() != null) {
            if (msg.getText().equals("Feedback")) {
                String standardAnswer = "Если у Вас есть вопросы или предложения по работе бота, обращайтесь:\n";
                answer.setText(standardAnswer + "@imperituro");
            } else if (msg.getText().equals("Set Item")) {
                String standardAnswer = "Отправте изображение с сообщением в формате (\'название\' \'размеры\' \'цена\')";
                answer.setText(standardAnswer);
                SetItemCommand.setActive(true);
            } else if(msg.getText().equals("Get Item")){
                String standardAnswer = "Напишите запрашиваемый элемент";
                answer.setText(standardAnswer);
                GetItemCommand.setActive(true);
            }else {
                answer.setText("Ваше сообщение не являеться командой");
            }
        }else{
            answer.setText("Ваше сообщение не являеться командой");
        }

        return answer;
    }

    private static String getUserName(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }
}
