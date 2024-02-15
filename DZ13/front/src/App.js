import {useState} from "react";
import "./App.css";

const baseUrl = "http://localhost:8080/json"

const OutputState = {
    Init: "init",
    Reading: "reading",
    Finished: "finished",
    Invalid: "invalid"
}

function App() {
    const [delay, setDelay] = useState(500);
    const [input, setInput] = useState("");
    const [output, setOutput] = useState("");
    const [cancelCallback, setCancelCallback] = useState(null);
    const [outputState, setOutputState] = useState(OutputState.Init);

    function cancelOutput() {
        if (OutputState.Reading && cancelCallback)
            cancelCallback();
    }

    function onEndOfStream() {
        try {
            JSON.parse(output);
        } catch (e) {
            setOutputState(OutputState.Invalid);
        }
        setOutputState(OutputState.Finished);
    }

    function onFluxFromInput() {
        onFlux("/stream-ndjson", input, 'POST');
    }

    function onFluxFromDB() {
        onFlux("/stream-ndjson/users", null, 'GET');
    }

    function readStream(processLine) {
        return function (response) {
            const stream = response.body.getReader();
            const matcher = /\r?\n/;
            const decoder = new TextDecoder();
            let buf = '';

            const loop = () =>
                stream.read().then(({done, value}) => {
                    if (done) {
                        if (buf.length > 0) processLine(JSON.parse(buf));
                    } else {
                        const chunk = decoder.decode(value, {
                            stream: true
                        });
                        buf += chunk;

                        const parts = buf.split(matcher);
                        buf = parts.pop();
                        for (const i of parts.filter(p => p)) processLine(JSON.parse(i));
                        return loop();
                    }
                });

            return {
                loop: loop(),
                cancel: () => stream.close()
            };
        }
    }

    function onFlux(prefix, body, method) {
        setOutputState(OutputState.Reading);
        fetch(
            baseUrl + `${prefix}/${delay}`,
            {
                method: method,
                mode: "cors",
                body: body,
                headers: {
                    Accept: "application/x-ndjson"
                }
            }
        )
            .then(response => {
                const parser = readStream(line => setOutput(prev => prev + JSON.stringify(line)));
                const {loop, cancel} = parser(response);
                setCancelCallback(cancel);
                loop();
            })
            .then(() => {
                setCancelCallback(null);
                onEndOfStream();
            });
    }

    function onSSE() {
        setOutputState(OutputState.Reading);
        const sse = new EventSource(baseUrl + `/stream-sse/users/${delay}`);
        sse.onerror = () => {
            if (sse.readyState === EventSource.CLOSED)
                onEndOfStream();
        }
        sse.onopen = () => {
            setCancelCallback(() => sse.close());
            sse.addEventListener("stream-sse-event", e => {
                setOutput(prev => prev + e.data);
            });
        }
    }

    function onCancel() {
        cancelOutput();
    }

    function onInput(event) {
        setInput(event.target.value);
    }

    function handleDelayChange(event) {
        const newDelay = event.target.value;
        if (/^\d*$/.test(newDelay))
            setDelay(newDelay);
    }

    return (
        <div className="App">
            <div className="column">
                <label htmlFor="in">Input JSON</label>
                <textarea id="in" value={input} onInput={onInput} style={{flex: 3}}/>
            </div>
            <div className="column input" style={{flex: 1}}>
                {outputState === OutputState.Reading ?
                    <button onClick={onCancel}>Cancel</button>
                    :
                    <>
                        <label htmlFor='delay'>Delay</label>
                        <input id='delay' onChange={handleDelayChange} value={delay}/>
                        <button onClick={onFluxFromInput}>Flux Input</button>
                        <button onClick={onFluxFromDB}>Flux Users</button>
                        <button onClick={onSSE}>SSE Users</button>
                    </>
                }
            </div>
            <div className="column">
                <label htmlFor="out">Output JSON</label>
                <textarea id="out" value={output} readOnly className={outputState} style={{flex: 3}}/>
            </div>
        </div>
    );
}

export default App;
