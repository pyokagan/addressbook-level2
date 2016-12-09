package seedu.addressbook.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList;
import seedu.addressbook.data.person.UniquePersonList.DuplicatePersonException;
import seedu.addressbook.data.tag.UniqueTagList;

public class AddCommandTest {
    private static final List<ReadOnlyPerson> LAST_SHOWN_LIST = Collections.emptyList();
    private static final Set<String> NO_TAGS = Collections.emptySet();

    @Test
    public void addCommand_invalidName_throws() {
        final String[] invalidNames = { "", " ", "[]\\[;]" };
        for (String name : invalidNames) {
            assertConstructingInvalidAddCmdThrows(name, Phone.EXAMPLE, false, Email.EXAMPLE, false,
                    Address.EXAMPLE, false, NO_TAGS);
        }
    }

    @Test
    public void addCommand_invalidPhone_throws() {
        final String[] invalidNumbers = { "", " ", "1234-5678", "[]\\[;]", "abc", "a123", "+651234" };
        for (String number : invalidNumbers) {
            assertConstructingInvalidAddCmdThrows(Name.EXAMPLE, number, false, Email.EXAMPLE, false,
                    Address.EXAMPLE, false, NO_TAGS);
        }
    }

    @Test
    public void addCommand_invalidEmail_throws() {
        final String[] invalidEmails = { "", " ", "def.com", "@", "@def", "@def.com", "abc@",
                "@invalid@email", "invalid@email!", "!invalid@email" };
        for (String email : invalidEmails) {
            assertConstructingInvalidAddCmdThrows(Name.EXAMPLE, Phone.EXAMPLE, false, email, false,
                    Address.EXAMPLE, false, NO_TAGS);
        }
    }

    @Test
    public void addCommand_invalidAddress_throws() {
        final String[] invalidAddresses = { "", " " };
        for (String address : invalidAddresses) {
            assertConstructingInvalidAddCmdThrows(Name.EXAMPLE, Phone.EXAMPLE, false, Email.EXAMPLE, false,
                    address, false, NO_TAGS);
        }
    }

    @Test
    public void addCommand_invalidTags_throws() {
        final String[][] invalidTags = { { "" }, { " " }, { "'" }, { "[]\\[;]" }, { "validTag", "" },
                { "", " " } };
        for (String[] tags : invalidTags) {
            Set<String> tagsToAdd = new HashSet<>(Arrays.asList(tags));
            assertConstructingInvalidAddCmdThrows(Name.EXAMPLE, Phone.EXAMPLE, false, Email.EXAMPLE, false,
                    Address.EXAMPLE, false, tagsToAdd);
        }
    }

    /**
     * Asserts that attempting to construct an add command with the supplied
     * invalid data throws an IllegalValueException
     */
    private void assertConstructingInvalidAddCmdThrows(String name, String phone, boolean isPhonePrivate,
            String email, boolean isEmailPrivate, String address, boolean isAddressPrivate,
            Set<String> tags) {
        try {
            new AddCommand(name, phone, isPhonePrivate, email, isEmailPrivate, address, isAddressPrivate,
                    tags);
        } catch (IllegalValueException e) {
            return;
        }
        String error = String.format(
                "An add command was successfully constructed with invalid input: %s %s %s %s %s %s %s %s",
                name, phone, isPhonePrivate, email, isEmailPrivate, address, isAddressPrivate, tags);
        fail(error);
    }

    @Test
    public void addCommand_validData_correctlyConstructed() throws IllegalValueException {
        Set<String> tags = new HashSet<>();
        AddCommand cmd = new AddCommand(Name.EXAMPLE, Phone.EXAMPLE, true, Email.EXAMPLE, false,
                Address.EXAMPLE, true, tags); // should never throw
        ReadOnlyPerson p = cmd.getPerson();

        assertEquals(Name.EXAMPLE, p.getName().fullName);
        assertEquals(Phone.EXAMPLE, p.getPhone().value);
        assertTrue(p.getPhone().isPrivate());
        assertEquals(Email.EXAMPLE, p.getEmail().value);
        assertFalse(p.getEmail().isPrivate());
        assertEquals(Address.EXAMPLE, p.getAddress().value);
        assertTrue(p.getAddress().isPrivate());
        boolean isTagListEmpty = !p.getTags().iterator().hasNext();
        assertTrue(isTagListEmpty);
    }

    @Test
    public void addCommand_emptyAddressBook_addressBookContainsPerson() {
        Person p = generateTestPerson();
        AddCommand cmd = new AddCommand(p);
        AddressBook book = new AddressBook();
        cmd.setData(book, LAST_SHOWN_LIST);
        CommandResult res = cmd.execute();
        UniquePersonList people = book.getAllPersons();

        assertTrue(people.contains(p));
        assertEquals(1, people.immutableListView().size());
        assertFalse(res.getRelevantPersons().isPresent());
        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, p), res.feedbackToUser);
    }

    @Test
    public void addCommand_addressBookAlreadyContainsPerson_addressBookUnmodified()
            throws DuplicatePersonException {
        Person p = generateTestPerson();
        AddressBook book = new AddressBook();
        book.addPerson(p); // should never throw
        AddCommand cmd = new AddCommand(p);
        cmd.setData(book, LAST_SHOWN_LIST);
        CommandResult res = cmd.execute();

        assertFalse(res.getRelevantPersons().isPresent());
        assertEquals(AddCommand.MESSAGE_DUPLICATE_PERSON, res.feedbackToUser);
        UniquePersonList people = book.getAllPersons();
        assertTrue(people.contains(p));
        assertEquals(1, people.immutableListView().size());
    }

    private static Person generateTestPerson() {
        try {
            return new Person(new Name(Name.EXAMPLE), new Phone(Phone.EXAMPLE, false),
                    new Email(Email.EXAMPLE, true), new Address(Address.EXAMPLE, false), new UniqueTagList());
        } catch (IllegalValueException e) {
            fail("test person data should be valid by definition");
            return null;
        }
    }
}
