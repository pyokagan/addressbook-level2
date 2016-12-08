package seedu.addressbook.addCommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import seedu.addressbook.commands.AddCommand;
import seedu.addressbook.commands.CommandResult;
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
	private AddressBook addressBook;
	private static final List<? extends ReadOnlyPerson> LAST_SHOWN_LIST = Collections.emptyList();
	private HashMap<String, String> valid;
	private AddCommand addCmd;

	@Before
	public void setup() {
		addressBook = new AddressBook();
		valid = new HashMap<String, String>();

	}

	/**
	 * Provides add command being tested the address book that it should use
	 */
	public void setAddressBook(AddressBook book) {
		assert (addCmd != null);
		addCmd.setData(book, LAST_SHOWN_LIST);
	}

	/*
	 * Note how the names of the test methods does not follow the normal naming
	 * convention. That is because our coding standard allows a different naming
	 * convention for test methods.
	 */

	@Test
	public void addCommand_invalidName_throws() {
		Set<String> tags = new HashSet<String>();
		final String[] invalidNames = { "", " ", "'", "[]\\[;]" };
		for (String name : invalidNames) {
			ConstructingInvalidAddCmdThrows(name, Phone.EXAMPLE, false, Email.EXAMPLE, false, Address.EXAMPLE, false,
					tags);
		}
	}

	@Test
	public void addCommand_invalidPhone_throws() {
		Set<String> tags = new HashSet<String>();
		final String[] invalidNumbers = { "", " ", "1234-5678", "[]\\[;]", "abc", "a123", "+651234" };
		for (String number : invalidNumbers) {
			ConstructingInvalidAddCmdThrows(Name.EXAMPLE, number, false, Email.EXAMPLE, false, Address.EXAMPLE, false,
					tags);
		}
	}

	@Test
	public void addCommand_invalidEmail_throws() {
		Set<String> tags = new HashSet<String>();
		final String[] invalidEmails = { "", " ", "def.com", "@", "@def", "@def.com", "abc@" };
		for (String email : invalidEmails) {
			ConstructingInvalidAddCmdThrows(Name.EXAMPLE, Phone.EXAMPLE, false, email, false, Address.EXAMPLE, false,
					tags);
		}
	}

	@Test
	public void addCommand_invalidAddress_throws() {
		Set<String> tags = new HashSet<String>();
		final String[] invalidAddresses = { "", " " };
		for (String address : invalidAddresses) {
			ConstructingInvalidAddCmdThrows(Name.EXAMPLE, Phone.EXAMPLE, false, Email.EXAMPLE, false, address, false,
					tags);
		}
	}

	@Test
	public void addCommand_invalidTags_throws() {
		final String[][] invalidTags = { { "" }, { " " }, { "'" }, { "validTag", "" }, { "", " " } };
		for (String[] tags : invalidTags) {
			Set<String> tagsToAdd = new HashSet<String>();
			for (String tag : tags) {
				tagsToAdd.add(tag);
			}
			ConstructingInvalidAddCmdThrows(Name.EXAMPLE, Phone.EXAMPLE, false, Email.EXAMPLE, false, Address.EXAMPLE,
					false, tagsToAdd);
		}
	}

	/**
	 * Asserts that attempting to construct an add command with the supplied
	 * invalid data throws an IllegalValueException
	 */
	private void ConstructingInvalidAddCmdThrows(String name, String phone, boolean isPhonePrivate, String email,
			boolean isEmailPrivate, String address, boolean isAddressPrivate, Set<String> tags) {
		try {
			AddCommand cmd = new AddCommand(name, phone, isPhonePrivate, email, isEmailPrivate, address,
					isAddressPrivate, tags);
			String errorFormatter = new String(
					"An add command was successfully constructed with invalid input: %s %s %s %s %s %s %s %s");
			String error = String.format(errorFormatter, name, phone, isPhonePrivate, email, isEmailPrivate, address,
					isAddressPrivate, tags);
			fail(error);
		} catch (IllegalValueException e) {
		}
	}

	@Test
	public void addCommand_validData_correctlyConstructed() {
		Set<String> tags = new HashSet<String>();
		try {
			AddCommand cmd = new AddCommand(Name.EXAMPLE, Phone.EXAMPLE, true, Email.EXAMPLE, false, Address.EXAMPLE,
					true, tags);
			ReadOnlyPerson p = cmd.getPerson();
			assertEquals(p.getName().fullName, Name.EXAMPLE);
			assertEquals(p.getPhone().value, Phone.EXAMPLE);
			assertTrue(p.getPhone().isPrivate());
			assertEquals(p.getEmail().value, Email.EXAMPLE);
			assertFalse(p.getEmail().isPrivate());
			assertEquals(p.getAddress().value, Address.EXAMPLE);
			assertTrue(p.getAddress().isPrivate());
			boolean isTagListEmpty = !p.getTags().iterator().hasNext();
			assertTrue(isTagListEmpty);
		} catch (IllegalValueException e) {
			fail("Adding command with valid data failed.");
		}
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
		assertTrue(countPeople(people) == 1);
		assertFalse(res.getRelevantPersons().isPresent());
		assertEquals(res.feedbackToUser, String.format(AddCommand.MESSAGE_SUCCESS, p));
	}

	@Test
	public void addCommand_addressBookAlreadyContainsPerson_addressBookUnmodified() {
		Person p = generateTestPerson();
		AddressBook book = new AddressBook();
		try {
			book.addPerson(p); // this should never throw
		} catch (DuplicatePersonException e) {
			fail("There is a problem with address book's addPerson method.");
		}
		AddCommand cmd = new AddCommand(p);
		cmd.setData(book, LAST_SHOWN_LIST);
		CommandResult res = cmd.execute();
		assertFalse(res.getRelevantPersons().isPresent());
		assertEquals(res.feedbackToUser, AddCommand.MESSAGE_DUPLICATE_PERSON);
		UniquePersonList people = book.getAllPersons();
		assertTrue(people.contains(p));
		assertTrue(countPeople(people) == 1);
	}

	private static Person generateTestPerson() {
		try {
			return new Person(new Name(Name.EXAMPLE), new Phone(Phone.EXAMPLE, false), new Email(Email.EXAMPLE, true),
					new Address(Address.EXAMPLE, false), new UniqueTagList());
		} catch (IllegalValueException e) {
			fail("test person data should be valid by definition");
			return null;
		}
	}

	/**
	 * Returns the number of people in a UniquePersonList This is necessary as
	 * UniquePersonList only exposes an iterator, and not its collection, so we
	 * need to count manually
	 */
	private int countPeople(UniquePersonList list) {
		int count = 0;
		for (ReadOnlyPerson p : list) {
			++count;
		}
		return count;
	}
}