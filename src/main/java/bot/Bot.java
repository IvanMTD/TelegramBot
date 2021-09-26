package bot;

import bot.command.AdminCommand;
import bot.command.GetItemCommand;
import bot.command.NonCommand;
import bot.command.SetItemCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import util.JsonReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Bot extends TelegramLongPollingCommandBot {

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    public Bot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        register(new AdminCommand("admin","Manual operation"));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (AdminCommand.isActive()) {
            setAnswer(AdminCommand.processMessage(update));
        } else if(SetItemCommand.isActive()){
            if(update.getMessage().hasPhoto()) {
                List<PhotoSize> photoSizes = update.getMessage().getPhoto();
                PhotoSize photoSize = photoSizes.get(photoSizes.size() - 1);
                String fileID = photoSize.getFileId();
                String urlAddress = "https://api.telegram.org/bot" + getBotToken() + "/getFile?file_id=" + fileID;
                String filePath = JsonReader.parseJson(urlAddress, "file_path");

                /*String urlPath = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
                System.out.println(urlPath);*/

                File file = null;
                try {
                    file = downloadFile(filePath);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                assert file != null;
                if (file.exists()) {
                    setAnswer(SetItemCommand.processMessage(update,file));
                }else{
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId().toString());
                    message.setText("Файл не существует, попробуйте снова");
                    SetItemCommand.setActive(false);
                    setAnswer(message);
                }
            }else{
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId().toString());
                message.setText("Отстутсвует изображение, попробуйте снова");
                SetItemCommand.setActive(false);
                setAnswer(message);
            }
        }else if(GetItemCommand.isActive()){
            setPhotoAnswer(GetItemCommand.processMessage(update));
            GetItemCommand.setActive(false);
        }else {
            setAnswer(NonCommand.getNonCommandAnswer(update));
        }

    }

    private ReplyKeyboardMarkup getKeyboardMarkup(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardButton information = new KeyboardButton("Feedback");
        keyboardRow1.add(information);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardButton setItem = new KeyboardButton("Set Item");
        keyboardRow2.add(setItem);

        KeyboardRow keyboardRow3 = new KeyboardRow();
        KeyboardButton getItem = new KeyboardButton("Get Item");
        keyboardRow3.add(getItem);

        List<KeyboardRow> keyboard = new ArrayList<>(Arrays.asList(keyboardRow2,keyboardRow3,keyboardRow1));

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private void setAnswer(SendMessage answer) {
        try {
            answer.enableMarkdown(true);
            ReplyKeyboardMarkup replyKeyboardMarkup = getKeyboardMarkup();
            answer.setReplyMarkup(replyKeyboardMarkup);
            execute(answer);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setPhotoAnswer(SendPhoto answer){
        try {
            ReplyKeyboardMarkup replyKeyboardMarkup = getKeyboardMarkup();
            answer.setReplyMarkup(replyKeyboardMarkup);
            execute(answer);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
