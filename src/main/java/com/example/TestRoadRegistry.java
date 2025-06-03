import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService service;

    public void setup() {
        service = new UserService();
    }

    public void testAddDemeritPointsWrongDate() {
        String result = service.addDemeritPoints("2024/03/03", 3);
        checkEqual("fail", result);
    }

    public void testAddDemeritPointsTooMany() {
        String result = service.addDemeritPoints("2024-03-03", 7);
        checkEqual("fail", result);
    }

    public void testAddDemeritPointsTooFew() {
        String result = service.addDemeritPoints("2024-03-03", 0);
        checkEqual("fail", result);
    }

    public void testAddDemeritPointsValid() {
        String result = service.addDemeritPoints("2024-03-03", 5);
        checkEqual("success", result);
    }

    public void testUpdateAllPersonalDetails() {
        boolean result = service.updatePersonalDetails("Senuka", "senuka123", "27-11-2005", "123 Test St", 19);
        checkTrue(result);
    }

    public void testChangeBirthdate() {
        boolean result = service.updatePersonalDetails(null, null, "23-03-2004", null, 20);
        checkTrue(result);
    }

    public void testUnderageUpdateDetails() {
        boolean result = service.updatePersonalDetails(null, null, "01-01-2008", "27 lollipop lane", 17);
        checkFalse(result);
    }

    public void testAddPersonCorrect() {
        boolean result = service.addPerson("User123!", "27 lollipop lane", "Johnson’s Av.", "Victoria");
        checkTrue(result);
    }

    public void testAddPersonIncorrectID() {
        boolean result = service.addPerson("1?!@s", "27 lollipop lane", "Main St", "Victoria");
        checkFalse(result);
    }

    public void testAddPersonDifferentState() {
        boolean result = service.addPerson("User456", "Low St.", "Street", "South Australia");
        checkTrue(result); 
    }

    public void testChangeNameAndBirthdayInvalid() {
        boolean result = service.updatePersonalDetails("john", null, "23-05-2003", null, 22);
        checkFalse(result);
    }

    public void testAddPersonWrongBirthday() {
        boolean result = service.addPersonWithBirthday("User789", "Main Rd", "Avenue", "Victoria", "1998-27-11");
        checkFalse(result);
    }

    public void testSuspendedRedPPlater() {
        boolean suspended = service.isSuspended(19, 6);
        checkTrue(suspended);
    }

    public void testSuspendedFullLicense() {
        boolean suspended = service.isSuspended(23, 13);
        checkTrue(suspended);
    }
}
