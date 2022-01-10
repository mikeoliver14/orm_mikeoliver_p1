package com.revature.models;

import java.util.Arrays;
import java.util.Objects;

public class League {
	
	public int leagueId;
	public int numOfTeams;
	
	
	public League() {
		super();
	}

	public League(int leagueId, int numOfTeams) {
		super();
		this.leagueId = leagueId;
		this.numOfTeams = numOfTeams;
	}

	public League(int numOfTeams) {
		super();
		this.numOfTeams = numOfTeams;
	}

	public int getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public int getNumOfTeams() {
		return numOfTeams;
	}

	public void setNumOfTeams(int numOfTeams) {
		this.numOfTeams = numOfTeams;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hash(leagueId, numOfTeams);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		League other = (League) obj;
		return leagueId == other.leagueId
				&& numOfTeams == other.numOfTeams;
	}

	@Override
	public String toString() {
		return "League [leagueId=" + leagueId + ", numOfTeams=" + numOfTeams + ", currentStandings=" + "]";
	}

}
