package fridgy.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import fridgy.commons.core.Messages;
import fridgy.commons.core.index.Index;
import fridgy.logic.commands.exceptions.CommandException;
import fridgy.model.IngredientModel;
import fridgy.model.ingredient.Ingredient;

/**
 * Deletes an ingredient identified using it's displayed index from the Inventory.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String INGREDIENT_KEYWORD = "ingredient";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + INGREDIENT_KEYWORD
            + ": Deletes the ingredient by index.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + INGREDIENT_KEYWORD + " 1";

    public static final String MESSAGE_DELETE_INGREDIENT_SUCCESS = "Deleted Ingredient:\n%1$s";

    private final Index targetIndex;

    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(IngredientModel model) throws CommandException {
        requireNonNull(model);
        List<Ingredient> lastShownList = model.getFilteredIngredientList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_INGREDIENT_DISPLAYED_INDEX);
        }

        Ingredient ingredientToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.delete(ingredientToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_INGREDIENT_SUCCESS, ingredientToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteCommand) other).targetIndex)); // state check
    }
}
