import java.util.Scanner;

/*
 * 文字列から数値を読み取るプログラム
 *   0                           -> 整数 (例: 0)
 *   [1-9][0-9]*                 -> 整数 (例: 100)
 *   0[xX][0-9a-fA-F]+           -> 整数 (例: 0xabc)
 *   [0-9]*[a-fA-F][0-9a-fA-F]*  -> 整数 (例: 0123456789a)
 *   [1-9][0-9]*\.[0-9]*         -> 小数 (例: 10.3)
 *   0\.[0-9]*                   -> 小数 (例: 0.12)
 *   \.[0-9]+                    -> 小数 (例: .12)
 */

public class Lexer {
	static class Token {
		static final String TYPE_INT = "INT";
		static final String TYPE_DEC = "DEC";
		static final String TYPE_ERR = "ERR";
		
		Token(String tokenType, int start, int len) {
			this.tokenType = tokenType;
			this.start = start;
			this.len = len;
		}
		
		String tokenType;  /* トークンの種類 */
		int start;         /* 文字列中のトークン開始位置 */
		int len;           /* トークンの長さ */
	}
	
	static final int CT_P = 0;
	static final int CT_X = 1;
	static final int CT_0 = 2;
	static final int CT_1 = 3;
	static final int CT_A = 4;
	static final int CT_OTHER = 5;

static int getCharType(int c) {
		if (c == '.')             return CT_P;
		if (c == 'x' || c == 'X') return CT_X;
		if (c == '0')             return CT_0;
		if ('1' <= c && c <= '9') return CT_1;
		if ('a' <= c && c <= 'f') return CT_A;
		if ('A' <= c && c <= 'F') return CT_A;
		return CT_OTHER;
	}
	
int[][] delta = {
    {4, -1,  1, 2, 3, -1}, // 初期状態(0) 受理×
	    {5, 6, 7, 7, 3, -1}, // 最初が0である状態(1) 受理○
	    {5, -1, 2, 2, 3, -1}, // 最初が[1-9]である状態(2) 受理○
	    {-1, -1, 3, 3, 3, -1}, // 最初が[a-f][A-F]である状態(3) 受理○
	    {-1, -1, 5, 5, -1, -1}, // 最初が.である状態(4) 受理×
	    {-1, -1, 5, 5, -1, -1}, // 小数部分がある状態(5) 受理○
	    {-1, -1, 3, 3, 3, -1}, // 0xまたは0Xである状態(6) 受理×
	    {-1, -1, 7, 7, 3, -1}, // 0[0-9]である状態(7) 受理×
	    
};

    Token getToken(String str, int start) {
		/* 現在注目している文字 (先頭から p 文字目)  */
		int p = start;

		/* 最後の受理状態のマーカとその時何文字目まで読んだか */
		String acceptMarker = Token.TYPE_ERR;
		int acceptPos = start;

		/* 現在の状態 */
		int currentState = 0;

		while (p < str.length()) {
			int c = str.charAt(p); /* str の p 文字目を読み取る */
			p++;
			
			int ct = getCharType(c);
			int nextState = delta[currentState][ct];

			/* TODO */
			/* 行先がなければループを抜ける */
			/* 行先が受理状態であれば「最後の受理状態」を更新する */

			if (nextState == -1) {
			    break;
			}
			
		        if (nextState == 1 || nextState == 2 || nextState == 3) {
				acceptMarker = Token.TYPE_INT;
				acceptPos = p;
			}
		        if (nextState == 5) {
				acceptMarker = Token.TYPE_DEC;
				acceptPos = p;
			}
						 
			    
			currentState = nextState;
		}
		
		return new Token(acceptMarker, start, acceptPos - start);
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String str = sc.nextLine();  /* 1行読み取る */
		Lexer lex = new Lexer();
		Token t = lex.getToken(str, 0);
		System.out.print(t.tokenType);
		System.out.println(str.substring(t.start, t.start + t.len));
	}
}
