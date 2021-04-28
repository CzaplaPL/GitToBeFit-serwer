package pl.umk.mat.git2befit.user.service.messaging.email;


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

    public static String getVerificationMessage(String token) {
        return "Witaj użytkowniku,\n" +
                "\nOto twój link do aktywacji konta na naszej platformie:\n" +
                "https://77.55.236.227:8443/user/activation/" +
                token +
                "\n\nW celu aktywacji konta wystarczy kliknąć w powyższy link.\n\n" +
                "Pozdrawiamy,\nGitToBeFit";
    }
}
