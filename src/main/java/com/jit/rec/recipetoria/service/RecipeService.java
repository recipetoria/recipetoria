package com.jit.rec.recipetoria.service;

import com.jit.rec.recipetoria.dto.IngredientDTO;
import com.jit.rec.recipetoria.dto.RecipeDTO;
import com.jit.rec.recipetoria.dto.TagDTO;
import com.jit.rec.recipetoria.entity.Ingredient;
import com.jit.rec.recipetoria.entity.Recipe;
import com.jit.rec.recipetoria.exception.ResourceNotFoundException;
import com.jit.rec.recipetoria.repository.IngredientRepository;
import com.jit.rec.recipetoria.repository.RecipeRepository;
import com.jit.rec.recipetoria.security.applicationUser.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final TagService tagService;

    private <T> void setPropertyValue(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    public List<RecipeDTO> getAllRecipes() {
        List<Recipe> allRecipes = recipeRepository.findAll();
        List<RecipeDTO> recipeResponses = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            recipeResponses.add(RecipeDTO.convertToDTO(recipe));
        }
        return recipeResponses;
    }

    public RecipeDTO createRecipe(RecipeDTO newRecipeInfo) {
        Recipe recipe = new Recipe();

        if (newRecipeInfo.getName() != null) {
            recipe.setName(newRecipeInfo.getName());

            setPropertyValue(newRecipeInfo.getInstructions(), recipe::setInstructions);
            setPropertyValue(newRecipeInfo.getLinks(), recipe::setLinks);
            setPropertyValue(newRecipeInfo.getInstructionPhotos(), recipe::setInstructionPhotos);
            setPropertyValue(newRecipeInfo.getMainPhoto(), recipe::setMainPhoto);

            // future logic for creation new tag at the same time as recipe
            // if only id in dto -> add tag(s) to recipe
            // if no id, but name -> create new tag for user, add tag to recipe

            Optional.ofNullable(newRecipeInfo.getTagDTOs())
                    .stream()
                    .flatMap(Collection::stream)
                    .map(TagDTO::id)
                    .map(tagService::getTagById)
                    .forEach(recipe.getTags()::add);


            Optional.ofNullable(newRecipeInfo.getIngredientDTOs())
                    .stream()
                    .flatMap(Collection::stream)
                    .map(IngredientDTO::convertToIngredient)
                    .map(ingredientRepository::save)
                    .forEach(recipe.getIngredientList()::add);

            recipe.setApplicationUser((ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            recipeRepository.save(recipe);
        }
        return RecipeDTO.convertToDTO(recipe);
    }

    public RecipeDTO getRecipeById(Long recipeId) {
        return RecipeDTO.convertToDTO(recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalStateException("Recipe with ID: " + recipeId + " not found!")));
    }

    public RecipeDTO updateRecipeById(Long recipeToUpdateId, RecipeDTO updatedRecipeDTO) {
        Recipe recipeToBeUpdated = recipeRepository.findById(recipeToUpdateId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe with ID: " + recipeToUpdateId + " not found!"));

        if (updatedRecipeDTO.getName() != null) {
            recipeToBeUpdated.setName(updatedRecipeDTO.getName());

            if (updatedRecipeDTO.getTagDTOs() != null) {
                recipeToBeUpdated.setTags(new ArrayList<>());
            }
            Optional.ofNullable(updatedRecipeDTO.getTagDTOs())
                    .stream()
                    .flatMap(Collection::stream)
                    .map(TagDTO::id)
                    .map(tagService::getTagById)
                    .forEach(recipeToBeUpdated.getTags()::add);

            if (updatedRecipeDTO.getIngredientDTOs() != null) {
                List<Ingredient> newIngredients = new ArrayList<>();
                for (IngredientDTO newIngredientDTO : updatedRecipeDTO.getIngredientDTOs()) {
                    Ingredient ingredient = IngredientDTO.convertToIngredient(newIngredientDTO);
                    newIngredients.add(ingredient);
                }
                recipeToBeUpdated.setIngredientList(newIngredients);
            }

            setPropertyValue(updatedRecipeDTO.getMainPhoto(), recipeToBeUpdated::setMainPhoto);
            setPropertyValue(updatedRecipeDTO.getInstructions(), recipeToBeUpdated::setInstructions);
            setPropertyValue(updatedRecipeDTO.getInstructionPhotos(), recipeToBeUpdated::setInstructionPhotos);
            setPropertyValue(updatedRecipeDTO.getLinks(), recipeToBeUpdated::setLinks);

        }
        return RecipeDTO.convertToDTO(recipeRepository.save(recipeToBeUpdated));
    }

    public void deleteRecipeById(Long recipeId) {
        if (!ingredientRepository.existsById(recipeId)) {
            throw new ResourceNotFoundException("Recipe with ID: " + recipeId + " not found!");
        }
        recipeRepository.deleteById(recipeId);
    }

}