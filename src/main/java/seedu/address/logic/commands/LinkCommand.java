package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CHILD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT;

import java.util.Set;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.LessonTime;
import seedu.address.model.person.Name;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.tag.Education;
import seedu.address.model.tag.Grade;
import seedu.address.model.tag.Tag;

/**
 * Links a Parent and a Student specified by their full names in a parent-child relationship.
 */
public class LinkCommand extends Command {

    public static final String COMMAND_WORD = "link";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Links a Parent and a Student in a parent-child relationship"
            + "Parameters: " + PREFIX_CHILD + "CHILD_NAME "
            + PREFIX_PARENT + "PARENT_NAME\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CHILD + "John Doe "
            + PREFIX_PARENT + "Jane Doe ";

    public static final String MESSAGE_SUCCESS = "Successfully linked Student: %1$s to Parent: %2$s";
    public static final String MESSAGE_PARENT_LINKED = "Parent: %1$s has an existing link to Student: %2$s";
    public static final String MESSAGE_CHILD_LINKED = "Student: %1$s has an existing link to Parent: %2$s";
    public static final String MESSAGE_PARENT_NOT_FOUND = "Parent: %1$s does not exist in Address Book";
    public static final String MESSAGE_CHILD_NOT_FOUND = "Student: %1$s does not exist in Address Book";

    private final Name childName;
    private final Name parentName;

    /**
     * Creates a {@code LinkCommand} from the specified {@code Name}s.
     */
    public LinkCommand(Name childName, Name parentName) {
        requireAllNonNull(childName, parentName);

        this.childName = childName;
        this.parentName = parentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Person parent;
        Person child;

        try {
            parent = model.personFromName(parentName);
            if (!(parent instanceof Parent)) {
                throw new CommandException(generateParentNotFoundMessage());
            }
        } catch (IllegalValueException e) {
            throw new CommandException(generateParentNotFoundMessage());
        }

        try {
            child = model.personFromName(childName);
            if (!(child instanceof Student)) {
                throw new CommandException(generateChildNotFoundMessage());
            }
        } catch (IllegalValueException e) {
            throw new CommandException(generateChildNotFoundMessage());
        }

        Parent castedParent = (Parent) parent;
        Student castedChild = (Student) child;

        if (castedParent.getChildName() != null) {
            throw new CommandException(generateParentLinkedMessage(castedParent.getChildName()));
        }
        if (castedChild.getParentName() != null) {
            throw new CommandException(generateChildLinkedMessage(castedChild.getParentName()));
        }

        Student linkedChild = createLinkedChild(castedChild, castedParent);
        Parent linkedParent = createLinkedParent(castedParent, castedChild);

        model.setPerson(castedChild, linkedChild);
        model.setPerson(castedParent, linkedParent);

        return new CommandResult(generateSuccessMessage());

    }

    private Student createLinkedChild(Student child, Parent parent) {
        Name name = child.getName();
        Phone phone = child.getPhone();
        Email email = child.getEmail();
        Address address = child.getAddress();
        LessonTime lessonTime = child.getLessonTime();
        Education education = child.getEducation();
        Grade grade = child.getGrade();
        Name parentName = parent.getName();
        Set<Tag> tags = child.getTags();
        boolean isPinned = child.getPinned();
        boolean isArchived = child.isArchived();

        return new Student(name, phone, email, address, lessonTime, education, grade, parentName, tags,
                isPinned, isArchived);

    }

    private Parent createLinkedParent(Parent parent, Student child) {
        Name name = parent.getName();
        Phone phone = parent.getPhone();
        Email email = parent.getEmail();
        Address address = parent.getAddress();
        Name childName = child.getName();
        Set<Tag> tags = parent.getTags();
        boolean isPinned = parent.getPinned();
        boolean isArchived = parent.isArchived();

        return new Parent(name, phone, email, address, childName, tags, isPinned, isArchived);
    }

    private String generateParentNotFoundMessage() {
        return String.format(MESSAGE_PARENT_NOT_FOUND, parentName.fullName);
    }

    private String generateParentLinkedMessage(Name name) {
        return String.format(MESSAGE_PARENT_LINKED, parentName.fullName, name.fullName);
    }

    private String generateChildNotFoundMessage() {
        return String.format(MESSAGE_CHILD_NOT_FOUND, childName.fullName);
    }

    private String generateChildLinkedMessage(Name name) {
        return String.format(MESSAGE_CHILD_LINKED, childName.fullName, name.fullName);
    }

    private String generateSuccessMessage() {
        return String.format(MESSAGE_SUCCESS, childName.fullName, parentName.fullName);
    }
}