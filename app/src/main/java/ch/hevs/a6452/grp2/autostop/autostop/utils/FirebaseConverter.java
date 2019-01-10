package ch.hevs.a6452.grp2.autostop.autostop.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class FirebaseConverter {
/*
    public static List<TeamEntity> toTeams(DataSnapshot snapshot) {
        List<TeamEntity> teams = new ArrayList<>();
        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
            TeamEntity entity = childSnapshot.getValue(TeamEntity.class);
            entity.setKey(childSnapshot.getKey());
            teams.add(entity);
        }
        return teams;
    }


    public static List<TournamentEntity> toTournaments(DataSnapshot snapshot) {
        List<TournamentEntity> tournaments = new ArrayList<>();
        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
            TournamentEntity entity = childSnapshot.getValue(TournamentEntity.class);
            entity.setKey(childSnapshot.getKey());
            tournaments.add(entity);
        }
        return tournaments;
    }


    public static List<PlayerEntity> toPlayers(DataSnapshot snapshot) {
        List<PlayerEntity> players = new ArrayList<>();
        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
            PlayerEntity entity = childSnapshot.getValue(PlayerEntity.class);
            entity.setKey(childSnapshot.getKey());
            players.add(entity);
        }
        return players;
    }*/

    public static String toNiceDateFormat(Long time){
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        return format.format(new Date(time));
    }

    /**
     * Generate a random uniq key
     * @return String the key
     */
    public static String generateKey(){
        return UUID.randomUUID().toString();
    }
}
