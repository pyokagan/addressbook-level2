package seedu.addressbook.addCommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.fail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import seedu.addressbook.commands.AddCommand;
import seedu.addressbook.commands.IncorrectCommand;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;

public class AddCommandTest {
	private AddressBook addressBook;
	private List<? extends ReadOnlyPerson> lastShownList = Collections.emptyList();
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
		addCmd.setData(book, lastShownList);
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
}