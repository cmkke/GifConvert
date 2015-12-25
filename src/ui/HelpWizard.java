package ui;

import javafx.fxml.FXMLLoader;
import javafx.stage.Window;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;

public class HelpWizard {

    public HelpWizard(Window owner) throws IOException {
        WizardPane page1 = new WizardPane();
        page1.setContent(FXMLLoader.load(HelpWizard.class.getResource("help_page1.fxml")));
        WizardPane page2 = new WizardPane();
        page2.setContent(FXMLLoader.load(HelpWizard.class.getResource("help_page2.fxml")));
        WizardPane page3 = new WizardPane();
        page3.setContent(FXMLLoader.load(HelpWizard.class.getResource("help_page3.fxml")));
        WizardPane page4 = new WizardPane();
        page4.setContent(FXMLLoader.load(HelpWizard.class.getResource("help_page4.fxml")));

        wizard = new Wizard(owner);
        wizard.setFlow(new Wizard.LinearFlow(page1, page2, page3, page4));
    }

    public void show() {
        wizard.showAndWait();
    }

    private Wizard wizard;

}
