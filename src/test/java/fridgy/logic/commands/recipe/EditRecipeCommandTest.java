package fridgy.logic.commands.recipe;

import static fridgy.logic.commands.recipe.EditRecipeCommand.EditRecipeDescriptor;
import static fridgy.logic.commands.recipe.EditRecipeCommand.MESSAGE_EDIT_RECIPE_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fridgy.commons.core.index.Index;
import fridgy.logic.commands.CommandResult;
import fridgy.logic.commands.exceptions.CommandException;
import fridgy.model.Model;
import fridgy.model.ModelManager;
import fridgy.model.recipe.Name;
import fridgy.model.recipe.Recipe;
import fridgy.model.recipe.Step;
import fridgy.testutil.EditRecipeDescriptorBuilder;
import fridgy.testutil.RecipeBuilder;

public class EditRecipeCommandTest {

    @Test
    public void constructor_nullIndexOrDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
            -> new EditRecipeCommand(null, new EditRecipeDescriptor()));
        assertThrows(NullPointerException.class, ()
            -> new EditRecipeCommand(Index.fromOneBased(1), null));
        assertThrows(NullPointerException.class, ()
            -> new EditRecipeCommand(null, null));
    }

    @Test
    public void execute_nullModelProvided_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
            -> new EditRecipeCommand(Index.fromZeroBased(1), new EditRecipeDescriptor()).execute(null));
    }

    @Test
    public void execute_invalidIndexProvided_throwsCommandException() {
        Index invalidIndexTooBig = Index.fromZeroBased(5);
        Index invalidIndexTooSmall = Index.fromZeroBased(0);
        EditRecipeDescriptor testDescriptor = new EditRecipeDescriptor();
        Model testModel = new ModelManager();
        EditRecipeCommand testCommand = new EditRecipeCommand(invalidIndexTooBig, testDescriptor);
        EditRecipeCommand testCommand2 = new EditRecipeCommand(invalidIndexTooSmall, testDescriptor);
        assertThrows(CommandException.class, () -> testCommand.execute(testModel));
        assertThrows(CommandException.class, () -> testCommand2.execute(testModel));
    }

    @Test
    public void execute_targetRecipeAlreadyExists_throwsCommandException() {
        Model testModel = new ModelManager();

        Recipe testRecipe1 = new RecipeBuilder()
                .withName("Test Name 1")
                .withDescription("Test Description")
                .withIngredients(Arrays.asList("Ingr1", "Ingr2"))
                .withSteps(Arrays.asList("Step 1", "Step 2"))
                .build();
        Recipe testRecipe2 = new RecipeBuilder()
                .withName("Test Name 2")
                .withDescription("Test Description")
                .withIngredients(Arrays.asList("Ingr1, Ingr2"))
                .withSteps(Arrays.asList("Step 1", "Step 2"))
                .build();

        testModel.add(testRecipe1);
        testModel.add(testRecipe2);

        EditRecipeDescriptor testDescriptor = new EditRecipeDescriptor();
        testDescriptor.setName(new Name("Test Name 2"));

        EditRecipeCommand testCommand = new EditRecipeCommand(Index.fromOneBased(1), testDescriptor);
        assertThrows(CommandException.class, () -> testCommand.execute(testModel));
    }

    @Test
    public void execute_validEditRecipeCommand_returnsCorrectResult() {
        Model testModel = new ModelManager();
        Recipe testRecipe1 = new RecipeBuilder()
                .withName("Test Name 1")
                .withDescription("Test Description")
                .withIngredients(Arrays.asList("Ingr1", "Ingr2"))
                .withSteps(Arrays.asList("Step 1", "Step 2"))
                .build();
        Recipe testRecipe2 = new RecipeBuilder()
                .withName("Test Name 2")
                .withDescription("Test Description")
                .withIngredients(Arrays.asList("Ingr1, Ingr2"))
                .withSteps(Arrays.asList("Step 1", "Step 2"))
                .build();
        testModel.add(testRecipe1);
        testModel.add(testRecipe2);

        EditRecipeDescriptor testDescriptor = new EditRecipeDescriptor();
        testDescriptor.setName(new Name("Test Name 3"));
        testDescriptor.setSteps(Arrays.asList(
                new Step("Step 1"),
                new Step("Step 2"),
                new Step("Step 3")));

        EditRecipeCommand testCommand = new EditRecipeCommand(Index.fromOneBased(1), testDescriptor);

        Model expectedModel = new ModelManager();
        Recipe targetRecipe = new RecipeBuilder()
                .withName("Test Name 3")
                .withDescription("Test Description")
                .withIngredients(Arrays.asList("Ingr1", "Ingr2"))
                .withSteps(Arrays.asList("Step 1", "Step 2", "Step 3"))
                .build();
        expectedModel.add(targetRecipe);
        expectedModel.add(testRecipe2);
        CommandResult expectedResult = new CommandResult(String
                .format(MESSAGE_EDIT_RECIPE_SUCCESS, targetRecipe));

        try {
            assertEquals(testCommand.execute(testModel), expectedResult);
            assertEquals(testModel, expectedModel);
        } catch (CommandException ce) {
            Assertions.fail("CommandException thrown!");
        }
    }

    @Test
    public void equals_sameEditRecipeCommandObject_returnsTrue() {
        EditRecipeCommand testCommand = new EditRecipeCommand(Index.fromOneBased(1), new EditRecipeDescriptor());
        assertEquals(testCommand, testCommand);
    }

    @Test
    public void equals_nullObject_returnsFalse() {
        EditRecipeCommand testCommand = new EditRecipeCommand(Index.fromOneBased(1), new EditRecipeDescriptor());
        assertNotEquals(testCommand, null);
    }

    @Test
    public void equals_otherEditRecipeCommandSameValues_returnsTrue() {
        EditRecipeDescriptor testDescriptor = new EditRecipeDescriptorBuilder()
                .withName("Test 1")
                .withSteps("Step 1", "Step 2", "Step 3")
                .withDescription("Optional description")
                .withIngredients("ingr1", "ingr2")
                .build();
        EditRecipeCommand testCommand = new EditRecipeCommand(Index.fromOneBased(1), testDescriptor);

        EditRecipeDescriptor targetDescriptor = new EditRecipeDescriptorBuilder()
                .withName("Test 1")
                .withSteps("Step 1", "Step 2", "Step 3")
                .withDescription("Optional description")
                .withIngredients("ingr1", "ingr2")
                .build();
        EditRecipeCommand targetCommand = new EditRecipeCommand(Index.fromOneBased(1), targetDescriptor);

        assertEquals(testCommand, targetCommand);
    }
}
