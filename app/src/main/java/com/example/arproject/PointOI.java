package com.example.arproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.io.Serializable;
import java.util.ArrayList;
import kotlin.Pair;

public class PointOI implements Serializable {

    public int id; //indentificador do ponto de interesse
    public float[] coords = { 0.0f, 0.0f }; //latitude, longitude
    public String icon; //icone do lugar
    public String title; //titulo do lugar
    public ArrayList<Pair<String, String>> images; //imagens associadas ao lugar
    public String pointdescription; //descrição do lugar
    public String[] urlfilesAr; //os arquivos da AR
    public String textAr; //texto da AR
    //public  String image;
    //public  String video;
    public float[] arCoords = {0.0f, 0.0f, 0.0f}; //orientção da AR

    public PointOI(){
        id=0;
        title = "";
        pointdescription = "";
        textAr = "";
    }

    public void setImages(ArrayList<Pair<String, String>> imgs){
        this.images = imgs;
    }

}