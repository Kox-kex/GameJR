package com.javarush.task.task32.task3209;

import com.javarush.task.task32.task3209.listeners.FrameListener;
import com.javarush.task.task32.task3209.listeners.TabbedPaneChangeListener;
import com.javarush.task.task32.task3209.listeners.UndoListener;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//класс представления
public class View extends JFrame implements ActionListener {
    private Controller controller;
    private JTabbedPane tabbedPane = new JTabbedPane();  //это будет панель с двумя вкладками
    private JTextPane htmlTextPane = new JTextPane();    //это будет компонент для визуального редактирования html
    //это будет компонент для редактирования html в виде текста, он будет отображать код html (теги и их содержимое)
    private JEditorPane plainTextPane = new JEditorPane();
    private UndoManager undoManager = new UndoManager ();
    private UndoListener undoListener = new UndoListener(undoManager);

    public View() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            ExceptionHandler.log(e);
        }
    }

    //Этот метод вызывается, когда произошла смена выбранной вкладки.
    public void selectedTabChanged() {
        if (tabbedPane.getSelectedIndex() == 0) {
            controller.setPlainText(plainTextPane.getText());
        }
        else if (tabbedPane.getSelectedIndex() == 1) {
            plainTextPane.setText(controller.getPlainText());
        }
        resetUndo();
    }

    //диалоговое окно
    public  void showAbout() {
        JOptionPane.showMessageDialog(tabbedPane, "Информация: ", "Окно информации",  JOptionPane.INFORMATION_MESSAGE);
    }

    //получает документ у контроллера и устанавливать его в панель редактирования htmlTextPane
    public void update() {
        htmlTextPane.setDocument(controller.getDocument());
    }

    public void selectHtmlTab() {
        tabbedPane.setSelectedIndex(0); //Выбирать html вкладку (переключаться на нее).
        resetUndo();
    }

    public boolean isHtmlTabSelected() {
        return tabbedPane.getSelectedIndex() == 0;
    }

    public void undo() {
        try {
            undoManager.undo();
        } catch (CannotUndoException e) {
            e.printStackTrace();
            ExceptionHandler.log(e);
        }
    }

    public void redo() {

        try {
            undoManager.redo();
        } catch (CannotRedoException e) {
            e.printStackTrace();
            ExceptionHandler.log(e);
        }

    }

    //Сбрасывать все правки с помощью метода, который ты реализовал ранее
    public void resetUndo() {
        undoManager.discardAllEdits();
    }

    public UndoListener getUndoListener() {
        return undoListener;
    }

    //инициализация контроллера
    public void init() {
        initGui();
        addWindowListener(new FrameListener(this));
        setVisible(true);
    }

    // наша панель меню
    public void initMenuBar () {
        JMenuBar jMenuBar = new JMenuBar();
        MenuHelper.initFileMenu(this, jMenuBar);
        MenuHelper.initEditMenu(this, jMenuBar);
        MenuHelper.initStyleMenu(this, jMenuBar);
        MenuHelper.initAlignMenu(this, jMenuBar);
        MenuHelper.initColorMenu(this, jMenuBar);
        MenuHelper.initFontMenu(this, jMenuBar);
        MenuHelper.initHelpMenu(this, jMenuBar);
        this.getContentPane().add(jMenuBar, BorderLayout.NORTH);
    }

    //метод инициализации панелей редактора
    public void initEditor() {
        /*htmlTextPane.setContentType("text/html");
        JScrollPane jScrollPane1 = new JScrollPane(htmlTextPane);
        tabbedPane.add( "HTML", jScrollPane1);
        JScrollPane jScrollPane2 = new JScrollPane(plainTextPane);
        tabbedPane.add( "Текст", jScrollPane2);
        tabbedPane.setPreferredSize(new Dimension());
        tabbedPane.addChangeListener(new TabbedPaneChangeListener(this));
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);*/
        htmlTextPane.setContentType("text/html");
        JScrollPane htmlTextScrollPane = new JScrollPane(htmlTextPane);
        tabbedPane.addTab("HTML", htmlTextScrollPane);
        JScrollPane plainTextScrollPane = new JScrollPane(plainTextPane);
        tabbedPane.addTab("Текст", plainTextScrollPane);
        Dimension dimension = new Dimension();
        dimension.setSize(640, 480);
        tabbedPane.setPreferredSize(dimension);
        TabbedPaneChangeListener tabbedPaneChangeListener = new TabbedPaneChangeListener(this);
        tabbedPane.addChangeListener(tabbedPaneChangeListener);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

    }

    public void initGui() {
        initMenuBar();
        initEditor();
        pack();
    }

    public void exit() {
        controller.exit();
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    //будет вызваться при выборе пунктов меню, у которых наше представление указано в виде слушателя событий.
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Новый": controller.createNewDocument();
            case "Открыть": controller.openDocument();
            case "Сохранить": controller.saveDocument();
            case "Сохранить как...": controller.saveDocumentAs();
            case "Выход": controller.exit();
            case "О программе": showAbout();
        }
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }
}

/*
    Графический интерфейс будет представлять собой окно, в котором будет меню и панель с двумя вкладками.
        На первой вкладке будет располагаться текстовая панель, которая будет отрисовывать html страницу. На ней можно будет форматировать и редактировать текст страницы.
        На второй вкладке будет редактор, который будет отображать код html страницы, в нем будут видны все используемые html теги. В нем также можно будет менять текст страницы, добавлять и удалять различные теги.
*/

