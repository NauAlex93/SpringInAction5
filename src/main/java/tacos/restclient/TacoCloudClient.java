package tacos.restclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tacos.Ingredient;
import tacos.Taco;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class TacoCloudClient {

    private RestTemplate rest;
    private Traverson traverson;

    public TacoCloudClient(RestTemplate rest, Traverson traverson) {
        this.rest = rest;
        this.traverson = traverson;
    }

    //
    // GET examples
    //

    /*
     * Specify parameter as varargs argument
     */
    public Ingredient getIngredientById(String ingredientId) {
        return rest.getForObject("http://localhost:8080/ingredients/{id}",
                Ingredient.class, ingredientId);
    }

    public List<Ingredient> getAllIngredients() {
        return rest.exchange("http://localhost:8080/ingredients",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Ingredient>>() {
                })
                .getBody();
    }

    //
    // PUT examples
    //

    public void updateIngredient(Ingredient ingredient) {
        rest.put("http://localhost:8080/ingredients/{id}",
                ingredient, ingredient.getId());
    }

    //
    // POST examples
    //
    public Ingredient createIngredient(Ingredient ingredient) {
        return rest.postForObject("http://localhost:8080/ingredients",
                ingredient, Ingredient.class);
    }

    //
    // DELETE examples
    //

    public void deleteIngredient(Ingredient ingredient) {
        rest.delete("http://localhost:8080/ingredients/{id}",
                ingredient.getId());
    }

    //
    // Traverson with RestTemplate examples
    //

    public Iterable<Ingredient> getAllIngredientsWithTraverson() {
        ParameterizedTypeReference<Resources<Ingredient>> ingredientType =
                new ParameterizedTypeReference<Resources<Ingredient>>() {
                };

        Resources<Ingredient> ingredientRes =
                traverson
                        .follow("ingredients")
                        .toObject(ingredientType);

        Collection<Ingredient> ingredients = ingredientRes.getContent();

        return ingredients;
    }

    public Ingredient addIngredient(Ingredient ingredient) {
        String ingredientsUrl = traverson
                .follow("ingredients")
                .asLink()
                .getHref();
        return rest.postForObject(ingredientsUrl,
                ingredient,
                Ingredient.class);
    }

    public Iterable<Taco> getRecentTacosWithTraverson() {
        ParameterizedTypeReference<Resources<Taco>> tacoType =
                new ParameterizedTypeReference<Resources<Taco>>() {
                };

        Resources<Taco> tacoRes =
                traverson
                        .follow("tacos")
                        .follow("recents")
                        .toObject(tacoType);

        return tacoRes.getContent();
    }
}
