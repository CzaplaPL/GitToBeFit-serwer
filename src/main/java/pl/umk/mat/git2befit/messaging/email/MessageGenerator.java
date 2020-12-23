package pl.umk.mat.git2befit.messaging.email;


//TODO komentarz
public class MessageGenerator {

    public static String getPasswordChangingMessage(String newPassword) {
        String msg = "Witaj użytkowniku,\n" +
                     "\nOto twoje nowe, wygenerowane hasło:\n" +
                     newPassword +
                     "\nPamiętaj, żeby jak najszybciej je zmienić.\n\n" +
                     "Pozdrawiamy,\nGitToBeFit";
        return msg;
    }
}
