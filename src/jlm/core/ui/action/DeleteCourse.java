package jlm.core.ui.action;

import jlm.core.model.Course;
import jlm.core.model.Game;
import jlm.core.model.ServerAnswer;
import jlm.core.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DeleteCourse extends AbstractGameAction {

    private Course course;
    private Component parentComponent;

    public DeleteCourse(Game game, String text, Component parentComponent) {
        super(game, text);
        course = game.getCurrentCourse();
        this.parentComponent = parentComponent;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (course.getCourseId() != null) {
            int choice = JOptionPane.showConfirmDialog(MainFrame.getInstance(), "Do you really want to delete the course on the server?");
            if (choice == JOptionPane.OK_OPTION) {
                String answer = course.delete();
                if (ServerAnswer.values()[Integer.parseInt(answer)] == ServerAnswer.WRONG_TEACHER_PASSWORD)
                    JOptionPane.showMessageDialog(parentComponent, "Wrong module teacher password", "Server error",
                            JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
