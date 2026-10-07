// utils/token.ts
export async function validateToken(token: string): Promise<boolean> {
    console.log('Validando token:', token)
  
    const validToken = 'token-valido'  // Token de exemplo que você pode considerar como válido
  
    // Em desenvolvimento, sempre valida o token
    if (process.env.NODE_ENV === 'development') {
      console.log('Ambiente de desenvolvimento - pulando validação real.')
      return true
    }
  
    // Em produção, apenas valida se o token corresponde ao token esperado
    return token === validToken
  }
  