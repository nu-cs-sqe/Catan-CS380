package i18n;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessagesTest {

  @AfterEach
  public void resetLocale() {
    Messages.setLocale(Locale.ENGLISH);
  }

  // English bundle returns the default text
  @Test
  public void testEnglishLookup() {
    Messages.setLocale(Locale.ENGLISH);
    Assertions.assertEquals("Roll Dice", Messages.get("button.roll"));
  }

  // Turkish bundle returns translated text (UTF-8 special chars)
  @Test
  public void testTurkishLookup() {
    Messages.setLocale(new Locale("tr"));
    Assertions.assertEquals("Zar At", Messages.get("button.roll"));
    Assertions.assertEquals("Şehir Kur",
        Messages.get("button.buildCity"));
  }

  // MessageFormat substitutes positional arguments
  @Test
  public void testArgumentSubstitution() {
    Messages.setLocale(Locale.ENGLISH);
    Assertions.assertEquals("Alice placed a settlement.",
        Messages.get("log.placed.settlement", "Alice"));
  }

  // Apostrophe in a parameterized English string is rendered literally
  @Test
  public void testApostropheEscaping() {
    Messages.setLocale(Locale.ENGLISH);
    Assertions.assertEquals("Bob's turn — Roll the dice",
        Messages.get("status.turn.roll", "Bob"));
  }
}
