package mezz.jei.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import mezz.jei.api.ingredients.IIngredients;

public class Ingredients implements IIngredients {
	private final ListMultimap<Class, List<Object>> inputs = ArrayListMultimap.create();
	private final ListMultimap<Class, Object> outputs = ArrayListMultimap.create();
	private boolean used = false; // check that the addon used this at all. legacy addons will not

	@Override
	public <T> List<List<T>> getInputs(Class<T> ingredientClass) {
		used = true;
		//noinspection unchecked
		return (List<List<T>>) (Object) inputs.get(ingredientClass);
	}

	@Override
	public <T> List<T> getOutputs(Class<T> ingredientClass) {
		used = true;
		//noinspection unchecked
		return (List<T>) outputs.get(ingredientClass);
	}

	public Map<Class, Collection> getInputIngredients() {
		Map<Class, Collection> inputIngredients = new HashMap<Class, Collection>();
		for (Map.Entry<Class, Collection<List<Object>>> entry : inputs.asMap().entrySet()) {
			Collection<Object> flatIngredients = new ArrayList<Object>();
			for (List<Object> ingredients : entry.getValue()) {
				flatIngredients.addAll(ingredients);
			}
			inputIngredients.put(entry.getKey(), flatIngredients);
		}
		return inputIngredients;
	}

	public Map<Class, Collection> getOutputIngredients() {
		//noinspection unchecked
		return (Map<Class, Collection>) (Object) outputs.asMap();
	}

	public boolean isUsed() {
		return used;
	}
}
