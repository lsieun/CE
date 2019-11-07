package gui;

public class Main {
    public static void main(String args[]) {
        ClassEditor classEdit = new ClassEditor();
        classEdit.setVisible(true);
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                try {
                    classEdit.chkNLoadClass(args[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // disable menu and buttons
            classEdit.setMenuAndButtonStatus();
        }
    }
}
