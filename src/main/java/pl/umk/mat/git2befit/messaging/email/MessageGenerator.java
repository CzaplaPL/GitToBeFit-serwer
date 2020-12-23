package pl.umk.mat.git2befit.messaging.email;


/**
 * {@code MessageGenerator} class generate a specified earlier Email messages and returns that as {@code String}.
 */
public class MessageGenerator {

    public static String getPasswordChangingMessage(String newPassword) {
        return "Witaj użytkowniku,\n" +
                     "\nOto twoje nowe, wygenerowane hasło:\n" +
                     newPassword +
                     "\nPamiętaj, żeby jak najszybciej je zmienić.\n\n" +
                     "Pozdrawiamy,\nGitToBeFit";
    }
}
