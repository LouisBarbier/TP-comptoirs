package comptoirs.service;

import comptoirs.dao.CommandeRepository;
import comptoirs.dao.ProduitRepository;
import comptoirs.entity.Commande;
import comptoirs.entity.Produit;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class CommandeServiceTest {
    private static final String ID_PETIT_CLIENT = "0COM";
    private static final String ID_GROS_CLIENT = "2COM";
    private static final String VILLE_PETIT_CLIENT = "Berlin";
    private static final BigDecimal REMISE_POUR_GROS_CLIENT = new BigDecimal("0.15");

    @Autowired
    private CommandeService service;

    @Autowired
    private CommandeRepository commandeDao;

    @Autowired
    private ProduitRepository produitDao;

    @Test
    void testCreerCommandePourGrosClient() {
        var commande = service.creerCommande(ID_GROS_CLIENT);
        assertNotNull(commande.getNumero(), "On doit avoir la clé de la commande");
        assertEquals(REMISE_POUR_GROS_CLIENT, commande.getRemise(),
            "Une remise de 15% doit être appliquée pour les gros clients");
    }

    @Test
    void testCreerCommandePourPetitClient() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertNotNull(commande.getNumero());
        assertEquals(BigDecimal.ZERO, commande.getRemise(),
            "Aucune remise ne doit être appliquée pour les petits clients");
    }

    @Test
    void testCreerCommandeInitialiseAdresseLivraison() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertEquals(VILLE_PETIT_CLIENT, commande.getAdresseLivraison().getVille(),
            "On doit recopier l'adresse du client dans l'adresse de livraison");
    }

    @Test
    void testCommandeExist (){
        assertThrows(Exception.class,
                () -> service.enregistreExpédition(-1),
                "La commande doit exister");
    }

    @Test
    void testCommandeDejaEnvoyee (){
        Commande comm=commandeDao.findById(99999).orElseThrow();
        assertTrue(comm.getEnvoyeele().isEqual(service.enregistreExpédition(99999).getEnvoyeele()),"La date ne doit pas changer");


        Produit prod=produitDao.findById(98).orElseThrow();
        assertEquals(17,prod.getUnitesEnStock(),"La quantité ne doit pas changer");
    }

    @Test
    void testBonneDate (){
        Commande comm=commandeDao.findById(99998).orElseThrow();
        assertTrue(LocalDate.now().isEqual(service.enregistreExpédition(99998).getEnvoyeele()),"La date d'envoye doit être aujourd'hui");
    }
}
