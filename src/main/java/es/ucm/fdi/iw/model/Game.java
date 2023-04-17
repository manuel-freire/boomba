package es.ucm.fdi.iw.model;

import lombok.*;

import java.util.*;

import javax.persistence.*;
import javax.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.ucm.fdi.iw.controller.UserController;

@Entity
@Data
@NoArgsConstructor
@Table(name="Game")
public class Game implements Transferable<Game.Transfer> {

    private static final int EXPLODING_TIME = 30;
    private static final int _NUMPLAYERS = 2;
    private static final int _IFX_LENGTH = 3;
    private static final String _TOPICS = "ALL";
    private static final String _ALPHABET = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
    private static final int _ROUNDS = 0;
    private static final boolean _PRIV = true;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    private int explodingTime;
    private int ifxLength;
    private int numPlayers;

    @Column
    @ElementCollection
    private List<Letter> alphabet;

    @Column
    @ElementCollection
    private List<Topic> topics;

    public enum GameState {
        LOBBY,
        GAME,
        FINISHED
    };

    private GameState state;
    private int rounds;
    private boolean priv;
    private String interfix;

    private String topicCode;

    @OneToOne
    private Player curr_player; 

    @OneToOne
    private Player creator; 

    @OneToMany
    @JoinColumn(name="game_id")
    private List<Player> players;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

    @Transactional
    public void initGame(User ucreator,  Player creator) {
        explodingTime = EXPLODING_TIME;
        ifxLength = _IFX_LENGTH;
        numPlayers = _NUMPLAYERS;
        interfix = "ITO";

        topicCode = UserController.generateRandomBase64Token(6);

        topics = new ArrayList<Topic>();
        for(String topic: Arrays.asList(_TOPICS.split(" "))){
            Topic n = new Topic();
            n.setB(true);
            n.setTopic(topic);
            topics.add(n);
        }
        
        alphabet = new ArrayList<Letter>();
        for(String letter: Arrays.asList(_ALPHABET.split(""))){
            Letter n = new Letter();
            n.setB(true);
            n.setLetter(letter);
            alphabet.add(n);
        }
            

        state =  GameState.LOBBY;
        rounds = _ROUNDS;
        priv = _PRIV;

        creator.initPlayer(this, ucreator, _ROUNDS);
        players = new ArrayList<Player>();
        players.add(creator);
        this.creator = creator;
    }

    public boolean playerExist(User user){
        for(Player p: players){
            if (p.getUser().getId() == user.getId())
                return true;
        }
        return false;
    }
}
