package comptoirs.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import comptoirs.dao.ProduitRepository;
import comptoirs.entity.Produit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class LigneServiceTest {
    static final int NUMERO_COMMANDE_DEJA_LIVREE = 99999;
    static final int NUMERO_COMMANDE_PAS_LIVREE  = 99998;
    static final int REFERENCE_PRODUIT_DISPONIBLE_1 = 93;
    static final int REFERENCE_PRODUIT_DISPONIBLE_2 = 94;
    static final int REFERENCE_PRODUIT_DISPONIBLE_3 = 95;
    static final int REFERENCE_PRODUIT_DISPONIBLE_4 = 96;
    static final int REFERENCE_PRODUIT_INDISPONIBLE = 97;
    static final int UNITES_COMMANDEES_AVANT = 0;

    @Autowired
    LigneService service;

    @Autowired
    private ProduitRepository produitDao;

    @Test
    void onPeutAjouterDesLignesSiPasLivre() {
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1);
        assertNotNull(ligne.getId(), "La ligne doit être enregistrée, sa clé générée");
    }

    @Test
    void laQuantiteEstPositive() {
        assertThrows(ConstraintViolationException.class, 
            () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 0),
            "La quantite d'une ligne doit être positive");
    }

    @Test
    void laQuantiteeDeProduitEstSuffisante(){
        assertThrows(Exception.class,
                () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, 99, 14),
                "La quantité ne devrait pas être suffisante");
    }

    @Test
    void laCommandeExiste(){
        assertThrows(Exception.class,
                () -> service.ajouterLigne(9999999, REFERENCE_PRODUIT_DISPONIBLE_1, 50),
                "La commande n'existe pas");
    }

    @Test
    void leProduitExiste(){
        assertThrows(Exception.class,
                () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, 100000000, 0),
                "Le produit n'existe pas");
    }

    @Test
    void quantiteDebitee (){
        Produit prod=produitDao.findById(99).orElseThrow();
        assertEquals(prod.getUnitesEnStock()-12,service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, 99, 12).getProduit().getUnitesEnStock(),"La quantitée n'a pas été débitée correctement");
    }

    @Test
    void commandeDejaLivree (){
        assertThrows(Exception.class,
                () -> service.ajouterLigne(NUMERO_COMMANDE_DEJA_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 0),
                "La commande a deja ete livree");
    }
}
