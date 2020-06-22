package com.javarush.task.task32.task3209;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

//класс контроллер
public class Controller {
    private View view;
    private HTMLDocument document = new HTMLDocument();
    private File currentFile;

    public Controller(View view) {
        this.view = view;
    }

    public HTMLDocument getDocument() {
        return document;
    }

    //должен получать текст из документа со всеми html тегами
    public String getPlainText() {
        String str = null;
        try (StringWriter stringWriter = new StringWriter();){
            new HTMLEditorKit().write(stringWriter, document, 0, document.getLength());
            str = stringWriter.toString();
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
        return str;
    }

    //записывать переданный текст с html тегами в документ document
    public void setPlainText(String text) {
            resetDocument();
        try( StringReader stringReader = new StringReader(text)) {
            new HTMLEditorKit().read(stringReader, document, 0);
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    //сбрасывать текущий документ
    public void resetDocument() {

        if (document != null) {
            document.removeUndoableEditListener(view.getUndoListener());
        }
        document = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        document.addUndoableEditListener(view.getUndoListener());
        view.update();
    }

    ////инициализация представления
    public void init() {
        createNewDocument();
    }

    public void exit() {
        System.exit(0);
    }

    //создания нового документа
    public void createNewDocument() {
        view.selectHtmlTab(); //Выбирать html вкладку у представления
        resetDocument(); //Сбрасывать текущий документ
        view.setTitle("HTML редактор"); //Устанавливать новый заголовок окна
        view.resetUndo(); //Сбрасывать правки в Undo менеджере
        currentFile = null; //Обнулить file
    }

    public void openDocument() {
        view.selectHtmlTab(); //Выбирать html вкладку у представления
        JFileChooser jFileChooser = new JFileChooser();//Создавать новый объект для выбора файла
        jFileChooser.setFileFilter(new HTMLFileFilter());//Устанавливать ему в качестве фильтра объект
        int ret = jFileChooser.showOpenDialog(view);//Показывать диалоговое окно "Open File" для выбора файла

        if (ret == JFileChooser.APPROVE_OPTION) {//Если пользователь подтвердит выбор файла:
            currentFile = jFileChooser.getSelectedFile();//Сохранять выбранный файл в поле currentFile
            resetDocument();//сбросить документ
            view.setTitle(currentFile.getName());
            try( FileReader fileReader = new FileReader(currentFile)) {
                new HTMLEditorKit().read(fileReader, document, 0);//Вычитать данные из FileReader-а в документ document с помощью объекта класса HTMLEditorKit
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
            view.resetUndo();//Сбросить правки


        }
    }


    public void saveDocument() {
        view.selectHtmlTab(); //Выбирать html вкладку у представления
        if (currentFile == null) saveDocumentAs();
        else {
            try (FileWriter fileWriter = new FileWriter(currentFile)) {
                new HTMLEditorKit().write(fileWriter, document, 0, document.getLength());
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }
    }

    //метод для сохранения файла под новым именем
    public void saveDocumentAs() {
        view.selectHtmlTab(); //Выбирать html вкладку у представления
        JFileChooser jFileChooser = new JFileChooser();//Создавать новый объект для выбора файла
        jFileChooser.setFileFilter(new HTMLFileFilter());//Устанавливать ему в качестве фильтра объект
        int ret = jFileChooser.showSaveDialog(view);//Показывать диалоговое окно "Save File" для выбора файла

        if (ret == JFileChooser.APPROVE_OPTION) {//Если пользователь подтвердит выбор файла:
            currentFile = jFileChooser.getSelectedFile();//Сохранять выбранный файл в поле currentFile
            view.setTitle(currentFile.getName());
            try (FileWriter fileWriter = new FileWriter(currentFile)) {
                new HTMLEditorKit().write(fileWriter, document, 0, document.getLength());
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }
    }

    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
        view.setController(controller);
        view.init();
        controller.init();


    }
}
