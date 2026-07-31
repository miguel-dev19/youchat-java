package cu.alexgi.youchat.items;

public class ItemEstadisticaPersonal {

    private String id;
    private int cant_msg_env;
    private int cant_msg_env_mg;
    private int cant_msg_rec;
    private int cant_msg_rec_mg;

    private int cant_img_env;
    private int cant_img_env_mg;
    private int cant_img_rec;
    private int cant_img_rec_mg;

    private int cant_aud_env;
    private int cant_aud_env_mg;
    private int cant_aud_rec;
    private int cant_aud_rec_mg;

    private int cant_arc_env;
    private int cant_arc_env_mg;
    private int cant_arc_rec;
    private int cant_arc_rec_mg;

    private int cant_sti_env;
    private int cant_sti_env_mg;
    private int cant_sti_rec;
    private int cant_sti_rec_mg;

    private int cant_est_rec;
    private int cant_est_rec_mg;

    private int cant_act_per_rec;
    private int cant_act_per_rec_mg;

    public ItemEstadisticaPersonal(String id, int cant_msg_env, int cant_msg_env_mg,
                                   int cant_msg_rec, int cant_msg_rec_mg, int cant_img_env,
                                   int cant_img_env_mg, int cant_img_rec, int cant_img_rec_mg,
                                   int cant_aud_env, int cant_aud_env_mg, int cant_aud_rec,
                                   int cant_aud_rec_mg, int cant_arc_env, int cant_arc_env_mg,
                                   int cant_arc_rec, int cant_arc_rec_mg, int cant_sti_env,
                                   int cant_sti_env_mg, int cant_sti_rec, int cant_sti_rec_mg,
                                   int cant_est_rec, int cant_est_rec_mg, int cant_act_per_rec,
                                   int cant_act_per_rec_mg) {
        this.id = id;
        this.cant_msg_env = cant_msg_env;
        this.cant_msg_env_mg = cant_msg_env_mg;
        this.cant_msg_rec = cant_msg_rec;
        this.cant_msg_rec_mg = cant_msg_rec_mg;
        this.cant_img_env = cant_img_env;
        this.cant_img_env_mg = cant_img_env_mg;
        this.cant_img_rec = cant_img_rec;
        this.cant_img_rec_mg = cant_img_rec_mg;
        this.cant_aud_env = cant_aud_env;
        this.cant_aud_env_mg = cant_aud_env_mg;
        this.cant_aud_rec = cant_aud_rec;
        this.cant_aud_rec_mg = cant_aud_rec_mg;
        this.cant_arc_env = cant_arc_env;
        this.cant_arc_env_mg = cant_arc_env_mg;
        this.cant_arc_rec = cant_arc_rec;
        this.cant_arc_rec_mg = cant_arc_rec_mg;
        this.cant_sti_env = cant_sti_env;
        this.cant_sti_env_mg = cant_sti_env_mg;
        this.cant_sti_rec = cant_sti_rec;
        this.cant_sti_rec_mg = cant_sti_rec_mg;
        this.cant_est_rec = cant_est_rec;
        this.cant_est_rec_mg = cant_est_rec_mg;
        this.cant_act_per_rec = cant_act_per_rec;
        this.cant_act_per_rec_mg = cant_act_per_rec_mg;
    }

    public ItemEstadisticaPersonal(String id) {
        this.id = id;
        this.cant_msg_env = 0;
        this.cant_msg_env_mg = 0;
        this.cant_msg_rec = 0;
        this.cant_msg_rec_mg = 0;
        this.cant_img_env = 0;
        this.cant_img_env_mg = 0;
        this.cant_img_rec = 0;
        this.cant_img_rec_mg = 0;
        this.cant_aud_env = 0;
        this.cant_aud_env_mg = 0;
        this.cant_aud_rec = 0;
        this.cant_aud_rec_mg = 0;
        this.cant_arc_env = 0;
        this.cant_arc_env_mg = 0;
        this.cant_arc_rec = 0;
        this.cant_arc_rec_mg = 0;
        this.cant_sti_env = 0;
        this.cant_sti_env_mg = 0;
        this.cant_sti_rec = 0;
        this.cant_sti_rec_mg = 0;

        this.cant_est_rec = 0;
        this.cant_est_rec_mg = 0;
        this.cant_act_per_rec = 0;
        this.cant_act_per_rec_mg = 0;
    }

    public int getCant_sti_env() {
        return cant_sti_env;
    }

    public void addCant_sti_env(int cant_sti_env) {
        this.cant_sti_env += cant_sti_env;
    }

    public int getCant_sti_env_mg() {
        return cant_sti_env_mg;
    }

    public void addCant_sti_env_mg(int cant_sti_env_mg) {
        this.cant_sti_env_mg += cant_sti_env_mg;
    }

    public int getCant_sti_rec() {
        return cant_sti_rec;
    }

    public void addCant_sti_rec(int cant_sti_rec) {
        this.cant_sti_rec += cant_sti_rec;
    }

    public int getCant_sti_rec_mg() {
        return cant_sti_rec_mg;
    }

    public void addCant_sti_rec_mg(int cant_sti_rec_mg) {
        this.cant_sti_rec_mg += cant_sti_rec_mg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCant_msg_env() {
        return cant_msg_env;
    }

    public void addCant_msg_env(int cant_msg_env) {
        this.cant_msg_env += cant_msg_env;
    }

    public int getCant_msg_env_mg() {
        return cant_msg_env_mg;
    }

    public void addCant_msg_env_mg(int cant_msg_env_mg) {
        this.cant_msg_env_mg += cant_msg_env_mg;
    }

    public int getCant_msg_rec() {
        return cant_msg_rec;
    }

    public void addCant_msg_rec(int cant_msg_rec) {
        this.cant_msg_rec += cant_msg_rec;
    }

    public int getCant_msg_rec_mg() {
        return cant_msg_rec_mg;
    }

    public void addCant_msg_rec_mg(int cant_msg_rec_mg) {
        this.cant_msg_rec_mg += cant_msg_rec_mg;
    }

    public int getCant_img_env() {
        return cant_img_env;
    }

    public void addCant_img_env(int cant_img_env) {
        this.cant_img_env += cant_img_env;
    }

    public int getCant_img_env_mg() {
        return cant_img_env_mg;
    }

    public void addCant_img_env_mg(int cant_img_env_mg) {
        this.cant_img_env_mg += cant_img_env_mg;
    }

    public int getCant_img_rec() {
        return cant_img_rec;
    }

    public void addCant_img_rec(int cant_img_rec) {
        this.cant_img_rec += cant_img_rec;
    }

    public int getCant_img_rec_mg() {
        return cant_img_rec_mg;
    }

    public void addCant_img_rec_mg(int cant_img_rec_mg) {
        this.cant_img_rec_mg += cant_img_rec_mg;
    }

    public int getCant_aud_env() {
        return cant_aud_env;
    }

    public void addCant_aud_env(int cant_aud_env) {
        this.cant_aud_env += cant_aud_env;
    }

    public int getCant_aud_env_mg() {
        return cant_aud_env_mg;
    }

    public void addCant_aud_env_mg(int cant_aud_env_mg) {
        this.cant_aud_env_mg += cant_aud_env_mg;
    }

    public int getCant_aud_rec() {
        return cant_aud_rec;
    }

    public void addCant_aud_rec(int cant_aud_rec) {
        this.cant_aud_rec += cant_aud_rec;
    }

    public int getCant_aud_rec_mg() {
        return cant_aud_rec_mg;
    }

    public void addCant_aud_rec_mg(int cant_aud_rec_mg) {
        this.cant_aud_rec_mg += cant_aud_rec_mg;
    }

    public int getCant_arc_env() {
        return cant_arc_env;
    }

    public void addCant_arc_env(int cant_arc_env) {
        this.cant_arc_env += cant_arc_env;
    }

    public int getCant_arc_env_mg() {
        return cant_arc_env_mg;
    }

    public void addCant_arc_env_mg(int cant_arc_env_mg) {
        this.cant_arc_env_mg += cant_arc_env_mg;
    }

    public int getCant_arc_rec() {
        return cant_arc_rec;
    }

    public void addCant_arc_rec(int cant_arc_rec) {
        this.cant_arc_rec += cant_arc_rec;
    }

    public int getCant_arc_rec_mg() {
        return cant_arc_rec_mg;
    }

    public void addCant_arc_rec_mg(int cant_arc_rec_mg) {
        this.cant_arc_rec_mg += cant_arc_rec_mg;
    }

    public int getCant_est_rec() {
        return cant_est_rec;
    }

    public void addCant_est_rec(int cant_est_rec) {
        this.cant_est_rec += cant_est_rec;
    }

    public int getCant_est_rec_mg() {
        return cant_est_rec_mg;
    }

    public void addCant_est_rec_mg(int cant_est_rec_mg) {
        this.cant_est_rec_mg += cant_est_rec_mg;
    }

    public int getCant_act_per_rec() {
        return cant_act_per_rec;
    }

    public void addCant_act_per_rec(int cant_act_per_rec) {
        this.cant_act_per_rec += cant_act_per_rec;
    }

    public int getCant_act_per_rec_mg() {
        return cant_act_per_rec_mg;
    }

    public void addCant_act_per_rec_mg(int cant_act_per_rec_mg) {
        this.cant_act_per_rec_mg += cant_act_per_rec_mg;
    }

    public int obtenerTotal(){
        int total = 0;
        total+=cant_msg_env_mg;
        total+=cant_msg_rec_mg;
        total+=cant_img_env_mg;
        total+=cant_img_rec_mg;
        total+=cant_aud_env_mg;
        total+=cant_aud_rec_mg;
        total+=cant_arc_env_mg;
        total+=cant_arc_rec_mg;
        total+=cant_sti_env_mg;
        total+=cant_sti_rec_mg;
        total+=cant_est_rec_mg;
        total+=cant_act_per_rec_mg;



        return total;
    }

    public int obtenerTotalRecibido(){
        int total = 0;
        total+=cant_msg_rec_mg;
        total+=cant_img_rec_mg;
        total+=cant_aud_rec_mg;
        total+=cant_arc_rec_mg;
        total+=cant_sti_rec_mg;
        total+=cant_est_rec_mg;
        total+=cant_act_per_rec_mg;
        return total;
    }

    public int obtenerTotalEnviado(){
        int total = 0;
        total+=cant_msg_env_mg;
        total+=cant_img_env_mg;
        total+=cant_aud_env_mg;
        total+=cant_arc_env_mg;
        total+=cant_sti_env_mg;
        return total;
    }
}
