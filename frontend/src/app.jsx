import { useState, useRef, useEffect } from 'react';
import './App.css';
import trilhaSonora from './assets/musica.mp3'
import noPlayerImg from './assets/no_player.webp';
import noClubImg from './assets/no_club.webp';

// ==========================================
// 1. COMPONENTE: CARTA DO JOGADOR (ESTILO FUT)
// ==========================================
function JogadorCard({ jogador, index }) {
    if (!jogador) return null;

    const countryCode = (jogador.nacionalidade || 'br').toLowerCase();
    const valorFormatado = jogador.value ? `€ ${Number(jogador.value).toFixed(2)} M` : '€ 0.00 M';

    const urlFoto = jogador.club || noClubImg;
    const urlEscudo = jogador.photo || noPlayerImg;

    return (
        <div className="jogador-card" style={{ animationDelay: `${index * 0.1}s` }}>

            {/* ================= TOPO DA CARTA ================= */}
            <div className="card-top-half">

                {/* HUD ESQUERDA: Overall, Posição, Bandeira e Escudo */}
                <div className="card-info-left">
                    <span className="card-fut-overall">{jogador.overall || 80}</span>

                    {/* VOLTOU PRA CÁ! */}
                    <span className="card-fut-pos">{jogador.pos}</span>

                    <img src={`https://flagcdn.com/w20/${countryCode}.png`} className="card-fut-flag" alt="" />
                    <img
                        src={urlEscudo}
                        className="card-fut-club"
                        alt=""
                        referrerPolicy="no-referrer"
                        onError={(e) => { e.target.onerror = null; e.target.src = noClubImg; }}
                    />
                </div>

                {/* FOTO DO JOGADOR */}
                <div className="card-photo-right">
                    <img
                        src={urlFoto}
                        className="card-fut-face"
                        alt=""
                        referrerPolicy="no-referrer"
                        onError={(e) => { e.target.onerror = null; e.target.src = noPlayerImg; }}
                    />
                </div>

            </div>

            {/* METADE INFERIOR: Nome e Valor */}
            <div className="card-bottom-half">
                <div className="card-fut-name">{jogador.fullName || jogador.name || 'Jogador'}</div>
                <div className="card-fut-value">{valorFormatado}</div>
            </div>

        </div>
    );
}

// ==========================================
// 2. COMPONENTE: CAMPO TÁTICO
// ==========================================
function CampoTatico({ listaJogadores }) {
    if (!listaJogadores || listaJogadores.length === 0) return null;

    const goleiros = listaJogadores.filter(p => p.pos === 'GK');
    const defesas = listaJogadores.filter(p => p.pos === 'DEF');
    const meios = listaJogadores.filter(p => p.pos === 'MID');
    const ataques = listaJogadores.filter(p => p.pos === 'ATK');

    return (
        <div className="linhas-formacao">
            <div className="linha-tatica">{ataques.map((j, i) => <JogadorCard key={i} jogador={j} index={i} />)}</div>
            <div className="linha-tatica">{meios.map((j, i) => <JogadorCard key={i} jogador={j} index={i + 2} />)}</div>
            <div className="linha-tatica">{defesas.map((j, i) => <JogadorCard key={i} jogador={j} index={i + 4} />)}</div>
            <div className="linha-tatica">{goleiros.map((j, i) => <JogadorCard key={i} jogador={j} index={i + 6} />)}</div>
        </div>
    );
}

// ==========================================
// 3. COMPONENTE PRINCIPAL (INTERFACE)
// ==========================================
function App() {
    // Estado Principal
    const [metodo, setMetodo] = useState('hill');
    const [orcamento, setOrcamento] = useState(100);

    // ARRAYS DE HISTÓRICO: Controlam o acúmulo de dados na lateral
    const [historico, setHistorico] = useState([]);
    const [idAtivo, setIdAtivo] = useState(null);

    // ====== CONTROLE DE ÁUDIO (PASTA PUBLIC) ======
    const audioRef = useRef(null);
    const [tocando, setTocando] = useState(false);

    const toggleMusica = () => {
        if (tocando) {
            audioRef.current.pause();
        } else {
            audioRef.current.play().catch(err => console.log("Aguardando interação inicial.", err));
        }
        setTocando(!tocando);
    };

    // Parâmetros Específicos
    const [tmax, setTmax] = useState(11);
    const [tempInicial, setTempInicial] = useState(100);
    const [tempFinal, setTempFinal] = useState(0.01);
    const [taxaReducao, setTaxaReducao] = useState(0.99);

    // Parâmetros do Genético
    const [tp, setTp] = useState(50);
    const [ng, setNg] = useState(100);
    const [tc, setTc] = useState(0.8);
    const [tm, setTm] = useState(0.05);
    const [ig, setIg] = useState(10);

    const nomesMetodos = {
        hill: "Subida de Encosta",
        hillt: "Subida de Encosta com Tentativas",
        simulada: "Têmpera Simulada",
        genetico: "Algoritmo Genético"
    };

    const calcularMetricas = (time) => {
        if (!time || time.length === 0) return { total: 0, medio: "0.0", custo: "0.0" };
        const total = time.reduce((acc, p) => acc + (p.overall || 0), 0);
        const medio = (total / time.length).toFixed(1);
        const custo = time.reduce((acc, p) => acc + (p.value || 0), 0).toFixed(1);
        return { total, medio, custo };
    };

    // Função do Botão 1: Gerar Inicial
    const gerarSolucaoInicial = async () => {
        try {
            const url = `/api/melhor-time?metodo=${metodo}&budget=${orcamento}&tmax=${tmax}&tempInicial=${tempInicial}&tempFinal=${tempFinal}&reducao=${taxaReducao}&tp=${tp}&ng=${ng}&tc=${tc}&tm=${tm}&ig=${ig}`;
            const resposta = await fetch(url);
            if (!resposta.ok) throw new Error(`Erro no servidor: ${resposta.status}`);

            const dados = await resposta.json();
            const novoId = Date.now();

            // MUDANÇA: Prepara a array vazia para as futuras otimizações
            const novaSimulacao = {
                id: novoId,
                inicial: dados.inicial,
                otimizacoes: []
            };

            setHistorico(prev => [novaSimulacao, ...prev]);
            setIdAtivo(novoId);
        } catch (erro) {
            console.error("Erro na API:", erro);
            alert("Erro ao conectar com o servidor.");
        }
    };

    const otimizar = async () => {
        if (!idAtivo) return;

        try {
            const url = `/api/melhor-time?metodo=${metodo}&budget=${orcamento}&tmax=${tmax}&tempInicial=${tempInicial}&tempFinal=${tempFinal}&reducao=${taxaReducao}&tp=${tp}&ng=${ng}&tc=${tc}&tm=${tm}&ig=${ig}`;
            const resposta = await fetch(url);
            if (!resposta.ok) throw new Error(`Erro no servidor: ${resposta.status}`);

            const dados = await resposta.json();

            const novaOtimizacao = {
                idOti: Date.now(),
                metodo: metodo,
                final: dados.final
            };

            // MUDANÇA: Preserva o histórico e adiciona a nova otimização no fim da fila
            setHistorico(prev => prev.map(sim =>
                sim.id === idAtivo ? {
                    ...sim,
                    otimizacoes: [...sim.otimizacoes, novaOtimizacao]
                } : sim
            ));
        } catch (erro) {
            console.error("Erro na API ao re-otimizar:", erro);
            alert("Erro ao conectar com o servidor.");
        }
    };

    // Função do Botão 3: Limpar tudo
    const limparHistorico = () => {
        setHistorico([]);
        setIdAtivo(null);
    };

// Prepara as variáveis visuais para o campo
    const simAtiva = historico.find(s => s.id === idAtivo);

    // Pega sempre a ÚLTIMA otimização gerada na array para desenhar o campo verde
    const ultimaOtimizacao = simAtiva?.otimizacoes?.length > 0
        ? simAtiva.otimizacoes[simAtiva.otimizacoes.length - 1]
        : null;

    return (
        <div className="app-container">

            {/* ===== LADO ESQUERDO: CAMPOS ===== */}
            <div className="pitches-area">

                <div className="pitch-column">
                    <div className="panel-header">SOLUÇÃO INICIAL</div>
                    <div className="pitch-bg">
                        {/* AQUI DESENHA A INICIAL */}
                        <CampoTatico
                            key={`ini-${simAtiva?.id}`}
                            listaJogadores={simAtiva?.inicial}
                        />
                    </div>
                </div>

                <div className="pitch-column">
                    <div className="panel-header">SOLUÇÃO FINAL</div>
                    <div className="pitch-bg">
                        {/* AQUI DESENHA A ÚLTIMA OTIMIZAÇÃO (SE ELA EXISTIR) */}
                        {ultimaOtimizacao && (
                            <CampoTatico
                                key={`fin-${ultimaOtimizacao.idOti}`}
                                listaJogadores={ultimaOtimizacao.final}
                            />
                        )}
                    </div>
                </div>

            </div>

            {/* ===== LADO DIREITO: CONTROLES ===== */}
            <div className="sidebar">

                {/* Áudio invisível */}
                <audio ref={audioRef} src={trilhaSonora} loop />

                {/* CONTAINER DO MÉTODO E BOTÃO ALINHADOS */}
                <div className="metodo-row-container">
                    <select className="dropdown-header" value={metodo} onChange={(e) => setMetodo(e.target.value)}>
                        <option value="hill">SUBIDA DE ENCOSTA</option>
                        <option value="hillt">SUBIDA DE ENCOSTA TENTATIVA</option>
                        <option value="simulada">TÊMPERA SIMULADA</option>
                        <option value="genetico">ALGORITMO GENÉTICO</option>
                    </select>

                    <button
                        className="btn-musica-quadrado"
                        onClick={toggleMusica}
                        title={tocando ? "Desligar Som" : "Ligar Som"}
                    >
                        {tocando ? (
                            /* Ícone de Som LIGADO (Outline) */
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{width: '55%', height: '55%'}}>
                                <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
                                <path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
                                <path d="M19.07 4.93a10 10 0 0 1 0 14.14"></path>
                            </svg>
                        ) : (
                            /* Ícone de Som MUTADO (Outline) */
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{width: '55%', height: '55%'}}>
                                <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
                                <line x1="23" y1="9" x2="17" y2="15"></line>
                                <line x1="17" y1="9" x2="23" y2="15"></line>
                            </svg>
                        )}
                    </button>
                </div>

                <div className="inputs-grid">
                    <div className="control-row">
                        <span>ORÇAMENTO:</span>
                        <input type="number" value={orcamento} onChange={(e) => setOrcamento(e.target.value)} />
                    </div>

                    {metodo === 'hillt' && (
                        <div className="control-row">
                            <span>T-MAX:</span>
                            <input type="number" value={tmax} onChange={(e) => setTmax(e.target.value)} />
                        </div>
                    )}

                    {metodo === 'simulada' && (
                        <>
                            <div className="control-row">
                                <span>TEMP. INICIAL:</span>
                                <input type="number" value={tempInicial} onChange={(e) => setTempInicial(e.target.value)} />
                            </div>
                            <div className="control-row">
                                <span>TEMP. FINAL:</span>
                                <input type="number" value={tempFinal} onChange={(e) => setTempFinal(e.target.value)} />
                            </div>
                            <div className="control-row">
                                <span>REDUÇÃO:</span>
                                <input type="number" step="0.01" value={taxaReducao} onChange={(e) => setTaxaReducao(e.target.value)} />
                            </div>
                        </>
                    )}

                    {metodo === 'genetico' && (
                        <>
                            <div className="control-row"><span>TP:</span><input type="number" value={tp} onChange={(e)=>setTp(e.target.value)} /></div>
                            <div className="control-row"><span>NG:</span><input type="number" value={ng} onChange={(e)=>setNg(e.target.value)} /></div>
                            <div className="control-row"><span>TC:</span><input type="number" step="0.1" value={tc} onChange={(e)=>setTc(e.target.value)} /></div>
                            <div className="control-row"><span>TM:</span><input type="number" step="0.01" value={tm} onChange={(e)=>setTm(e.target.value)} /></div>
                            <div className="control-row"><span>IG:</span><input type="number" value={ig} onChange={(e)=>setIg(e.target.value)} /></div>
                        </>
                    )}
                </div>

                <div className="button-group">
                    <button className="btn-simular" onClick={gerarSolucaoInicial}>
                        GERAR S. INICIAL
                    </button>
                    <button className="btn-simular btn-otimizar" onClick={otimizar} disabled={!idAtivo}>
                        OTIMIZAR
                    </button>
                </div>

                {/* ===== CAIXA DE RESULTADOS COM SUPORTE A LIMPEZA ===== */}
                <div className="results-box">
                    <div className="results-header">
                        <span>RESULTADOS</span>
                        <button className="btn-limpar" onClick={limparHistorico}>
                            LIMPAR
                        </button>
                    </div>

                    <div className="results-content">
                        {historico.length === 0 ? (
                            <p>Selecione um método e clique em GERAR INICIAL.</p>
                        ) : (
                            historico.map((sim) => {
                                const metricasInicial = calcularMetricas(sim.inicial);

                                return (
                                    <div key={sim.id} className="historico-item">
                                        <p>SOLUÇÃO INICIAL</p>
                                        <p>OVERALL TOTAL: {metricasInicial.total}</p>
                                        <p>OVERALL MÉDIO: {metricasInicial.medio}</p>
                                        <p>CUSTO TOTAL: € {metricasInicial.custo} M</p>

                                        {/* LOOP INTERNO: Imprime todas as re-otimizações em cascata */}
                                        {sim.otimizacoes.length === 0 ? (
                                            <p style={{ color: '#212121', fontStyle: 'italic', marginTop: '12px' }}>
                                                [Aguardando clique em OTIMIZAR]
                                            </p>
                                        ) : (
                                            sim.otimizacoes.map((oti, index) => {
                                                const metricasFinal = calcularMetricas(oti.final);
                                                const ganhoPercentual = metricasInicial.total > 0
                                                    ? ((100 * Math.abs(metricasInicial.total - metricasFinal.total)) / metricasInicial.total).toFixed(2)
                                                    : "0.00";

                                                return (
                                                    <div key={oti.idOti} style={{ marginTop: '15px', paddingLeft: '8px', borderLeft: '2px solid rgba(0,0,0,0.1)' }}>
                                                        <p style={{ margin: '4px 0', color: '#1a472a', fontWeight: 'bold' }}>
                                                            [ OTIMIZAÇÃO #{index + 1} ] {nomesMetodos[oti.metodo]}
                                                        </p>
                                                        <p>SOLUÇÃO FINAL</p>
                                                        <p>OVERALL TOTAL: {metricasFinal.total}</p>
                                                        <p>OVERALL MÉDIO: {metricasFinal.medio}</p>
                                                        <p>CUSTO TOTAL: € {metricasFinal.custo} M</p>
                                                        <p style={{ marginTop: '4px', fontWeight: 'bold', color: '#9E380D' }}>
                                                            GANHO TOTAL %: {ganhoPercentual}%
                                                        </p>
                                                    </div>
                                                );
                                            })
                                        )}
                                        <hr className="historico-divisor" />
                                    </div>
                                );
                            })
                        )}
                    </div>
                </div>

            </div>
        </div>
    );
}

export default App;