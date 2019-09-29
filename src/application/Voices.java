package application;

public enum Voices {

    Voice1 ("(voice_kal_diphone)"),
    Voice2 ("(voice_akl_nz_jdt_diphone)"),
    Voice3 ("(voice_akl_nz_cw_cg_cg)")
    ;

    private final String voicesPackage;

    private Voices(String voicesPackage) {
        this.voicesPackage = voicesPackage;
    }

    public String getVoicePackage() {
        return voicesPackage;
    }
}
