import io.javalin.Javalin;
import model.Player;
import service.Algorithms;
import service.BackpackService;
import service.GeneticService;
import util.ReadCSV;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main(String[] args) {

        // 1. LIGA O SERVIDOR E CARREGA A SUA TELA DO REACT
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
            config.staticFiles.add("frontend/dist", io.javalin.http.staticfiles.Location.EXTERNAL);
        }).start(7070);

        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(new java.net.URI("http://localhost:7070"));
                }
            }
        } catch (Exception e) {
            System.out.println("Aviso: O Java não conseguiu abrir o navegador sozinho.");
        }

        app.get("/api/melhor-time", ctx -> {

            // 2. PREPARA OS ARQUIVOS E CLASSES (Roda só uma vez ao ligar o PC)
            ReadCSV reader = new ReadCSV();
            BackpackService bs = new BackpackService();
            Algorithms al = new Algorithms();

            String[] files = { "GK_FC.csv", "DEF_FC.csv", "MID_FC.csv", "ATK_FC.csv" };
            String[] keys = { "GK", "DEF", "MID", "ATK" };
            Map<String, List<Player>> players = reader.readFiles(files, keys);

            double[] pWeights = { 1.0, 1.0, 1.0, 1.0 };
            int[] formation = { 1, 4, 4, 2}; // GK, DEF, MID, ATK
            Map<String, Double> posWeights = bs.genPosWeights(keys, pWeights);

            System.out.println("SERVIDOR ONLINE! Acesse no navegador: http://localhost:7070");

            String metodo = ctx.queryParam("metodo");
            if (metodo == null) metodo = "hill"; // Se não vier nada, começa pelo Hill Climbing

            // Lógica segura para o ORÇAMENTO
            String budgetParam = ctx.queryParam("budget");
            double budget = (budgetParam != null && !budgetParam.isEmpty()) ? Double.parseDouble(budgetParam) : 100.0;

            List<Player> timeInicial = new ArrayList<>();
            List<Player> timeFinal = new ArrayList<>();

            // Gera a inicial
            timeInicial = bs.genFirstSolution(players, formation, budget, keys);
            double[] teamInfo = bs.evaluate(timeInicial, posWeights);

            // Roteamento dos algoritmos
            if ("hill".equals(metodo)) {
                timeFinal = al.hillClimbing(players, posWeights, new ArrayList<>(timeInicial), teamInfo.clone(), budget, 6);
            }
            else if ("hillt".equals(metodo)) {
                String tmaxParam = ctx.queryParam("tmax");
                int tmax = (tmaxParam != null && !tmaxParam.isEmpty()) ? Integer.parseInt(tmaxParam) : 11;

                timeFinal = al.hillClimbingT(players, posWeights, new ArrayList<>(timeInicial), teamInfo.clone(), budget, tmax/2, tmax);
            }
            else if ("simulada".equals(metodo)) {
                String tempInicialParam = ctx.queryParam("tempInicial");
                double tempInicial = (tempInicialParam != null && !tempInicialParam.isEmpty()) ? Double.parseDouble(tempInicialParam) : 100.0;

                String tempFinalParam = ctx.queryParam("tempFinal");
                double tempFinal = (tempFinalParam != null && !tempFinalParam.isEmpty()) ? Double.parseDouble(tempFinalParam) : 0.01;

                String reducaoParam = ctx.queryParam("reducao");
                double reducao = (reducaoParam != null && !reducaoParam.isEmpty()) ? Double.parseDouble(reducaoParam) : 0.99;

                timeFinal = al.simulatedAnnealing(new ArrayList<>(timeInicial), teamInfo.clone(), players, posWeights, budget, tempInicial, tempFinal, reducao);
            }
            else if ("genetico".equals(metodo)) {

                GeneticService gn = new GeneticService(players, formation, budget, keys, posWeights);

                String tpParam = ctx.queryParam("tp");
                int tp = (tpParam != null && !tpParam.isEmpty()) ? Integer.parseInt(tpParam) : 50;

                String ngParam = ctx.queryParam("ng");
                int ng = (ngParam != null && !ngParam.isEmpty()) ? Integer.parseInt(ngParam) : 100;

                String tcParam = ctx.queryParam("tc");
                double tc = (tcParam != null && !tcParam.isEmpty()) ? Double.parseDouble(tcParam) : 0.8;

                String tmParam = ctx.queryParam("tm");
                double tm = (tmParam != null && !tmParam.isEmpty()) ? Double.parseDouble(tmParam) : 0.05;

                String igParam = ctx.queryParam("ig");
                int ig = (igParam != null && !igParam.isEmpty()) ? Integer.parseInt(igParam) : 10;

                timeFinal = al.(players, budget, tp, ng, tc, tm, ig);
            }

            // 4. PREPARA A RESPOSTA E MANDA PRO REACT
            Map<String, Object> resposta = Map.of(
                    "inicial", timeInicial,
                    "final", timeFinal
            );

            ctx.json(resposta);
        });
        // FIM DA ROTA DA API

    } // FIM DO MAIN
}