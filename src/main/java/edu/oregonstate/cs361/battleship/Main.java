package edu.oregonstate.cs361.battleship;

import com.google.gson.Gson;
import spark.Request;

import java.io.UnsupportedEncodingException;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Main {

    public static void main(String[] args) {
        //This is for debugging purposes
//        if (true) {
//            String projectDir = System.getProperty("user.dir");
//            String staticDir = "/src/main/resources/public";
//            staticFiles.externalLocation(projectDir + staticDir);
//        } else {
//            staticFiles.location("/public");
//        }

        staticFiles.location("/public");

        //This will listen to GET requests to /model and return a clean new model
        get("/model", (req, res) -> newModel());
        //This will listen to POST requests and expects to receive a game model, as well as location to fire to
        post("/fire/:row/:col", (req, res) -> fireAt(req));
        //This will listen to POST requests and expects to receive a game model, as well as location to scan at
        post("/scan/:row/:col", (req, res) -> Scan(req));
        //This will listen to POST requests and expects to receive a game model, as well as location to place the ship
        post("/placeShip/:id/:row/:col/:orientation", (req, res) -> placeShip(req));
    }

    //This function returns a new model
    private static String newModel() {
        BattleshipModel bm = new BattleshipModel();
        Gson gson = new Gson();
        return gson.toJson(bm);
    }

    //This function accepts an HTTP request and deseralizes it into an actual Java object.
    private static BattleshipModel getModelFromReq(Request req){
        Gson gson = new Gson();
        String result = "";
        try {
            result = java.net.URLDecoder.decode(req.body(),"US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BattleshipModel modelFromReq = gson.fromJson(result, BattleshipModel.class);
        return modelFromReq;
    }

    //This controller
    private static String placeShip(Request req) {
        BattleshipModel currModel = getModelFromReq(req);
        String id = req.params("id");
        String row = req.params("row");
        String col = req.params("col");
        String orientation = req.params("orientation");
        currModel = currModel.placeShip(id,row,col,orientation,currModel);
        Gson gson = new Gson();
        return gson.toJson(currModel);

    }

    private static String fireAt(Request req) {

        BattleshipModel currModel = getModelFromReq(req);
        String row = req.params("row");
        String col = req.params("col");
        int rowInt = Integer.parseInt(row);
        int colInt = Integer.parseInt(col);
        currModel.shootAtComputer(rowInt,colInt);
        currModel.shootAtPlayer();
        Gson gson = new Gson();
        return gson.toJson(currModel);

    }

    private static String Scan(Request req) {

        BattleshipModel currModel = getModelFromReq(req);
     //   String results;
        String row = req.params("row");
        String col = req.params("col");
        int rowInt = Integer.parseInt(row);
        int colInt = Integer.parseInt(col);

        if (currModel.scanPlayer(rowInt,colInt)){
            currModel.results = "Yes";
        }else if(colInt > 1 && currModel.scanPlayer(rowInt,colInt-1)){
            currModel.results = "Yes";
        }else if(rowInt > 1 && currModel.scanPlayer(rowInt-1,colInt)){
            currModel.results = "Yes";
        }else if(colInt < 10 && currModel.scanPlayer(rowInt,colInt+1)){
            currModel.results = "Yes";
        }else if(rowInt < 10 && currModel.scanPlayer(rowInt+1,colInt)){
            currModel.results = "Yes";
        }else{
            currModel.results = "No";
        }
        currModel.shootAtPlayer();
    //    return currModel.results;
        Gson gson = new Gson();
        return gson.toJson(currModel);
    }

}