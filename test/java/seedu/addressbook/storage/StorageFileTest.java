package seedu.addressbook.storage;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.tag.UniqueTagList;
import seedu.addressbook.storage.StorageFile.InvalidStorageFilePathException;
import seedu.addressbook.storage.StorageFile.StorageOperationException;

public class StorageFileTest {
    private static final String TEST_DATA_FOLDER = "./test/java/seedu/addressbook/storage";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void initStorageFile_nullFilePath_exceptionThrown() throws InvalidStorageFilePathException {
        thrown.expect(NullPointerException.class);
        new StorageFile(null);
    }

    @Test
    public void initStorageFile_invalidFilePath_exceptionThrown() throws InvalidStorageFilePathException {
        thrown.expect(IllegalValueException.class);
        new StorageFile(TEST_DATA_FOLDER + "/InvalidFileName");
    }

    @Test
    public void load_invalidFormat_exceptionThrown() throws InvalidStorageFilePathException, StorageOperationException {
        StorageFile storage = new StorageFile(TEST_DATA_FOLDER + "/InvalidData.txt");
        thrown.expect(StorageOperationException.class);
        storage.load();
    }

    @Test
    public void load_validFormat() throws StorageOperationException, IllegalValueException {
        StorageFile storage = new StorageFile(TEST_DATA_FOLDER + "/ValidData.txt");
        AddressBook ab = storage.load();
        Person testPerson = new Person(new Name("John Doe"), new Phone("98765432", false),
                new Email("johnd@gmail.com", false), new Address("John street, block 123, #01-01", false),
                new UniqueTagList(Collections.emptySet()));
        
        // ensure loaded AddressBook Object is properly constructed with sample data
        assertTrue(ab.getAllPersons().contains(testPerson));
    }

    @Test
    public void save_nullAddressBook_exceptionThrown()
            throws InvalidStorageFilePathException, StorageOperationException {
        StorageFile storage = new StorageFile(TEST_DATA_FOLDER + "/temp.txt");
        thrown.expect(NullPointerException.class);
        storage.save(null);
    }

    @Test
    public void save_validAddressBook() throws IllegalValueException, StorageOperationException, IOException {
        StorageFile storage = new StorageFile(TEST_DATA_FOLDER + "/temp.txt");
        AddressBook ab = new AddressBook();
        Person testPerson = new Person(new Name("John Doe"), new Phone("98765432", false),
                new Email("johnd@gmail.com", false), new Address("John street, block 123, #01-01", false),
                new UniqueTagList(Collections.emptySet()));
        ab.addPerson(testPerson);
        storage.save(ab);

        // ensure xml data for sample data is properly saved
        assertTrue(compareFile("/temp.txt", "/ValidData.txt"));
    }

    private boolean compareFile(String file1, String file2) throws IOException {
        String s1 = new String(Files.readAllBytes(Paths.get(TEST_DATA_FOLDER + file1)));
        String s2 = new String(Files.readAllBytes(Paths.get(TEST_DATA_FOLDER + file2)));
        return s1.equals(s2);
    }
}