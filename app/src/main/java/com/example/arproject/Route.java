package com.example.arproject;

import java.util.ArrayList;

public class Route {

    //Atributos da rota
    public int id;
    public float version;
    public String name;
    public String desc;
    public ArrayList<PointOI> lugares;

    public Route(){
        id=0;
        name = "LugarNome";
        desc = "nada ainda";
        version = 1.0f;
        lugares = new ArrayList<PointOI>();
    }
}
