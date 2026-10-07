<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { enviarMensagemChat, type ChatMessage } from '../services/api'

const mensagens = ref<ChatMessage[]>([])
const pergunta = ref('')
const carregando = ref(false)
const erro = ref('')
const listaRef = ref<HTMLElement | null>(null)

const sugestoes = [
  'Quantos funcionários cada empresa possui?',
  'Quem trabalhou mais horas nos últimos 30 dias?',
  'Quais funcionários não registraram ponto neste mês?'
]

const rolarParaFim = async () => {
  await nextTick()
  listaRef.value?.scrollTo({ top: listaRef.value.scrollHeight, behavior: 'smooth' })
}

const enviar = async (texto?: string) => {
  const conteudo = (texto ?? pergunta.value).trim()
  if (!conteudo || carregando.value) return

  const historico = mensagens.value.slice(-10)
  mensagens.value.push({ role: 'user', content: conteudo })
  pergunta.value = ''
  erro.value = ''
  carregando.value = true
  await rolarParaFim()

  try {
    const { data } = await enviarMensagemChat(conteudo, historico)
    mensagens.value.push({ role: 'assistant', content: data.answer })
  } catch (e: any) {
    erro.value = e?.response?.data?.erro ?? 'Não foi possível obter uma resposta do assistente.'
  } finally {
    carregando.value = false
    await rolarParaFim()
  }
}

// Enter envia; Shift+Enter quebra a linha
const enviarComEnter = (e: KeyboardEvent) => {
  if (e.shiftKey) return
  e.preventDefault()
  enviar()
}

const limparConversa = () => {
  mensagens.value = []
  erro.value = ''
}
</script>

<template>
  <div class="grid justify-items-center">
    <UCard class="w-11/12 md:w-4/5">
      <template #header>
        <div class="flex justify-between items-center w-full flex-wrap gap-2">
          <div>
            <h2 class="text-xl font-bold">Chat IA</h2>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Pergunte sobre empresas, funcionários e registros de ponto. Processado localmente com Mistral via Ollama.
            </p>
          </div>
          <UButton
            v-if="mensagens.length"
            color="white"
            icon="heroicons:trash"
            :disabled="carregando"
            @click="limparConversa"
          >
            Limpar conversa
          </UButton>
        </div>
      </template>

      <div ref="listaRef" class="h-[55vh] overflow-y-auto space-y-3 pr-1">
        <div v-if="!mensagens.length" class="h-full flex flex-col items-center justify-center gap-3 text-center">
          <UIcon name="mdi:robot-outline" class="w-12 h-12 text-blue-400" />
          <p class="text-gray-500 dark:text-gray-400">Experimente uma das perguntas abaixo:</p>
          <div class="flex flex-wrap justify-center gap-2">
            <UButton
              v-for="sugestao in sugestoes"
              :key="sugestao"
              color="white"
              size="sm"
              @click="enviar(sugestao)"
            >
              {{ sugestao }}
            </UButton>
          </div>
        </div>

        <div
          v-for="(mensagem, indice) in mensagens"
          :key="indice"
          class="flex"
          :class="mensagem.role === 'user' ? 'justify-end' : 'justify-start'"
        >
          <div
            class="max-w-[80%] rounded-2xl px-4 py-2 whitespace-pre-line"
            :class="mensagem.role === 'user'
              ? 'bg-blue-200 text-black'
              : 'bg-gray-100 dark:bg-gray-800'"
          >
            {{ mensagem.content }}
          </div>
        </div>

        <div v-if="carregando" class="flex justify-start">
          <div class="rounded-2xl px-4 py-2 bg-gray-100 dark:bg-gray-800 text-gray-500 flex items-center gap-2">
            <UIcon name="mdi:loading" class="w-5 h-5 animate-spin" />
            Pensando...
          </div>
        </div>
      </div>

      <UAlert
        v-if="erro"
        class="mt-3"
        color="red"
        variant="soft"
        icon="heroicons:exclamation-triangle"
        :title="erro"
      />

      <template #footer>
        <form class="flex gap-2 items-end" @submit.prevent="enviar()">
          <UTextarea
            v-model="pergunta"
            class="flex-grow"
            :rows="2"
            autoresize
            placeholder="Digite sua pergunta... (Enter envia, Shift+Enter quebra linha)"
            :disabled="carregando"
            @keydown.enter="enviarComEnter"
          />
          <UButton
            type="submit"
            color="primary"
            icon="heroicons:paper-airplane"
            :loading="carregando"
            :disabled="!pergunta.trim()"
          >
            Enviar
          </UButton>
        </form>
      </template>
    </UCard>
  </div>
</template>
