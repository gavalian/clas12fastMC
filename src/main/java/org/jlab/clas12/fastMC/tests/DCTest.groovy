package org.jlab.clas12.fastMC.tests


import org.jlab.clas12.fastMC.base.DetectorHit
import org.jlab.clas12.fastMC.detectors.DCDetector
import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer
import org.jlab.clas12.fastMC.tools.FileFinder
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.geom.prim.Path3D
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.ParticleList
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.LundReader

List<String> dataFiles = FileFinder.getFiles("/media/tylerviducic/Elements/clas12/mcdata/*.dat");
System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");
H2F hSquare = new H2F("hSquare", "hSquare",200, -450, 450, 200, -450, 450);
TCanvas c1 = new TCanvas("c1", 500, 500);

dcDetector = new DCDetector();
ParticleSwimmer swimmer = new ParticleSwimmer();

int eventCounter = 0;

for(String dataFile: dataFiles) {

    System.out.println(dataFile)
    LundReader reader = new LundReader();
    reader.acceptStatus(1);
    reader.addFile(dataFile);
    reader.open();

    PhysicsEvent event = new PhysicsEvent();

    while (reader.nextEvent(event)) {
        eventCounter++;
        println(event.toLundString());
        event.setBeamParticle(new Particle(11, 0, 0, 11));
        event.setTargetParticle(new Particle(2212, 0, 0, 0));

        ParticleList particles = event.getParticleList();
        for(int i = 0; i < particles.count(); i++){
            Path3D particlePath = swimmer.getParticlePath(particles.get(i));
            List<DetectorHit> hits = dcDetector.getHits(particlePath);
            if (hits.size() > 0){
                println("has hits");
                for (DetectorHit hit: hits){
                    hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                }
            }
        }
        if (eventCounter > 100000){
            break;
        }
    }
}

c1.draw(hSquare);

