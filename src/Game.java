import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable {

    //função de grafico do canvas
    public static JFrame frame;
    //Criar a thread do meu jogo
    private Thread thread;
    //Verificar se o jogo esta rodando e assim permitir o looping
    private boolean isRunning=true;
    //Comprimento
    private final int WIDTH=240;
    //Largura
    private final int HEIGHT= 160;
    //Escala para poder dimensionar
    private final int SCALE=3;
    //Variavel para definir o padrao da imagem usada
    private BufferedImage image;

    private Spritesheet sheet;
    private BufferedImage[] player;
    private int frames=0;
    private final int maxFrames=10;
    private int curAnimatio=0;
    private final int maxAnimations=3;

    //Construtor do game para inicializar as variaveis
    public Game(){
        sheet=new Spritesheet("/spriteSheet.png");
        player=new BufferedImage[4];
        player[0]= sheet.getSprite(0,0,16,16);
        player[1]= sheet.getSprite(16,0,16,16);
        player[2]= sheet.getSprite(32,0,16,16);
        player[3]= sheet.getSprite(48,0,16,16);
        //Ajustar o tamanho da tela
        this.setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
        //funçao para iniciar o frame da tela
        initframe();
        //Criando a variavel imagem com os valos Globais e usando o padrao de cores RGB
        image= new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_BGR);
    }
    public void initframe(){
        //Criando o frame da tela
        frame= new JFrame("Game #1");
        //Adicionando essa classe no frame
        frame.add(this);
        //Nao permitir que a pessoa rescale minha tela
        frame.setResizable(false);
        //depois de adicionar o canva, ele calcula as dimenções e mostra pra gnt
        frame.pack();
        //fazer com que a tela se inicie no centro
        frame.setLocationRelativeTo(null);
        //Fazendo com que quando clicar para sair do jogo ele realmente nao sair e nao executar em segundo plano
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Fazer com que o conteudo da minha tela seja realmente visivel
        frame.setVisible(true);
    }
    //função para inicializar o jogo
    public synchronized void start(){
        //inicia a thread com esse objeto
        thread=new Thread(this);
        //faz o looping começar a rodar
        isRunning=true;
        //inicia a thread
        thread.start();

    }
    //função para parar o jogo
    public synchronized void stop(){
        //Parar o looping se algo der errado
        isRunning=false;
        //Tentando pegar algum erro ao parar o programa
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //função para atualizar o jogo
    public void update(){
        frames++;
        if(frames>maxFrames){
            frames=0;
            curAnimatio++;
            if(curAnimatio>maxAnimations){
                curAnimatio=0;
            }
        }
    }
    //função para redenrizar o jogo
    public void render(){
        //Sequencia de buffer que a gnt pega para otimizar a redenrização
        BufferStrategy bs= this.getBufferStrategy();
        //caso nao tenha criado ainda
        if(bs == null){
            //ele vai criar aqui
            this.createBufferStrategy(3);
            return;
        }
        //Cria uma variavel de grafico pegando os Graficos da imagem criada em cima
        Graphics g = image.getGraphics();
        //Define a cor que vai aparecer na tela
        g.setColor(new Color(9, 42, 109));
        //Desenha um retangulo nas posiçoes colocadas
        g.fillRect(0,0,WIDTH,HEIGHT);
        /*Renderização do jogo*/
        Graphics2D g2=(Graphics2D) g;
        g2.drawImage(player[curAnimatio],20,20,null);
        //Melhorar a perfomace
        g.dispose();
        //Pega os graficos desenhados no buffer
        g=bs.getDrawGraphics();
        //Desenha a imagem da escala que eu passei como parametro
        g.drawImage(image,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
        //Mostra o Grafico
        bs.show();

    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
    public void run() {
        //Pegar horario do pc de forma precisa
        long lastTime= System.nanoTime();
        //Quantidade de atualizações que eu quero por segundo
        double amoutOfTicks=60.0;
        //Utilizado para o calculo para quando ele tem que dar o update
        double ns= 1000000000/amoutOfTicks;
        //variavel utilizada para sabe quando der um segundo
        double delta=0;
        //Utilizado para fazer um debug do fps do jogo
        int frames=0;
        //Um tempo menos preciso mas suficiente para debbugar
        double timer=System.currentTimeMillis();
        while(isRunning){
            //pegar o tempo constantemente para atualizar
            long now=System.nanoTime();
            //conta para saber quando bater o 1 segundo de forma precisa
            delta+=(now-lastTime)/ns;
            //Para sempre atualizar o valor da Ultima hora
            lastTime=now;
            if(delta>=1){
                //chamando função para atualizar o jogo
                update();
                //chamando função para redenrizar o jogo
                render();
                //incrementa para printar o fps pro segundo
                frames ++;
                //para garantir que o delta nao vai ter erros por conta do computador
                delta--;

            }
            if(System.currentTimeMillis()-timer >=1000){
                //printa o valor do FPS
                System.out.println("Fps: "+frames);
                //zera o valor do frames
                frames=0;
                //aumenta o timer para nao ser chamado de novo logo em seguida
                timer+=1000;
            }
        }
        stop();

    }
}
