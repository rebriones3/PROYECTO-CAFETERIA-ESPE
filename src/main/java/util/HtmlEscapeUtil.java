package util;

/**
 * Utilidad para escapar HTML y prevenir ataques XSS (Cross-Site Scripting)
 * 
 * Esta clase proporciona métodos seguros para escapar caracteres especiales
 * que podrían ser interpretados como código HTML/JavaScript malicioso.
 */
public class HtmlEscapeUtil {
    
    /**
     * Escapa caracteres especiales de HTML para prevenir XSS
     * 
     * @param input String de entrada (puede contener datos de usuario)
     * @return String con caracteres HTML escapados de forma segura
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        
        StringBuilder escaped = new StringBuilder(input.length() + 20);
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            switch (c) {
                case '<':
                    escaped.append("&lt;");
                    break;
                case '>':
                    escaped.append("&gt;");
                    break;
                case '&':
                    escaped.append("&amp;");
                    break;
                case '"':
                    escaped.append("&quot;");
                    break;
                case '\'':
                    escaped.append("&#x27;");
                    break;
                case '/':
                    escaped.append("&#x2F;");
                    break;
                default:
                    // Escapar caracteres de control y caracteres especiales
                    if (c < 32 || c > 126) {
                        escaped.append("&#").append((int) c).append(";");
                    } else {
                        escaped.append(c);
                    }
                    break;
            }
        }
        
        return escaped.toString();
    }
    
    /**
     * Versión simplificada para mensajes de error donde solo necesitamos
     * escapar los caracteres más críticos
     * 
     * @param input String de entrada
     * @return String escapado
     */
    public static String escapeForErrorMessage(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
}
